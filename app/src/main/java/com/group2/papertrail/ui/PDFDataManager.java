package com.group2.papertrail.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.telecom.Call;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group2.papertrail.dao.PDFDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.util.Callback;
import com.group2.papertrail.util.FilePicker;
import com.group2.papertrail.util.PDFManager;
import com.group2.papertrail.util.SharedPreferencesManager;
import com.group2.papertrail.util.ThumbnailManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class PDFDataManager {
    private static PDFDataManager instance;
    private MutableLiveData<List<PDF>> pdfFiles;

    private final MutableLiveData<Boolean> isDataChanged = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private SharedPreferencesManager sharedPreferencesManager;
    private final Context ctx;

    private PDFDAO pdfDAO;

    public enum PDFOperationResult {
        SUCCESS,
        ERROR,
        EMPTY_FILE,
        DUPLICATE
    }

    public enum BooleanCallback {
        TRUE,
        FALSE
    }

    private PDFDataManager(Context context) {
        this.pdfFiles = new MutableLiveData<>(new ArrayList<PDF>());
        this.ctx = context.getApplicationContext();
        this.pdfDAO = new PDFDAO(this.ctx);
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance(context.getApplicationContext());
    }

    public static synchronized PDFDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PDFDataManager(context);
        }
        return instance;
    }

    public MutableLiveData<List<PDF>> getPdfFiles() {
        return pdfFiles;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean state) {
        this.isLoading.setValue(state);
    }

    public void loadPDF(Callback<PDFOperationResult> callback) {
        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<PDF> pdfs = pdfDAO.findAllByUserId(sharedPreferencesManager.getUserId());
                new Handler(Looper.getMainLooper()).post(() -> {
                    pdfFiles.setValue(pdfs);
                    setIsLoading(false);
                    isDataChanged.setValue(false);
                    callback.onResult(PDFOperationResult.SUCCESS);
                });
            } catch (Exception e) {
                Log.e("PDF_VM", "Error loading pdf", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(PDFOperationResult.ERROR);
                });
            }
        });
    }

    private String savePdfLocally(Uri pdfUri) {
        try {
            Cursor cursor = ctx.getContentResolver().query(pdfUri, null, null, null, null);
            String fileName = "shared_file.pdf"; // Default name
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }

            // Save the file to local storage
            var outputDir = new File(ctx.getFilesDir(), "shared_pdfs");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);
            var inputStream = ctx.getContentResolver().openInputStream(pdfUri);
            var outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addPDF(FilePicker.FileMetadata metadata, Category category, boolean hasFileContent, Callback<PDFOperationResult> callback) {
        if (metadata == null || category == null) {
            callback.onResult(PDFOperationResult.EMPTY_FILE);
            return;
        }

        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {

                String sharedPdfUri = null;
                if(hasFileContent) {
                    sharedPdfUri = savePdfLocally(metadata.getFileUri());
                }

                if(hasFileContent && sharedPdfUri == null) {
                    callback.onResult(PDFOperationResult.ERROR);
                    return;
                }

                var thumbnailBitmap = ThumbnailManager.generateThumbnailFromURI(this.ctx, metadata.getFileUri());
                var thumbnailURI = ThumbnailManager.saveThumbnailToStorage(this.ctx, thumbnailBitmap);
                var pdfMetadata = PDFManager.readPDFMetadata(this.ctx, metadata.getFileUri());

                var newPdf = new PDF(
                        metadata.getFileName(),
                        sharedPdfUri == null ? metadata.getFileUri().toString() : sharedPdfUri,
                        thumbnailURI,
                        pdfMetadata.getTitle(),
                        pdfMetadata.getAuthor(),
                        metadata.getFileSize(),
                        pdfMetadata.getPageCount(),
                        pdfMetadata.getCreationDate() != null ? pdfMetadata.getCreationDate() : new Date(),
                        category,
                        pdfMetadata.getCreationDate() != null,
                        sharedPreferencesManager.getUserId()
                );

                var res = pdfDAO.insert(newPdf);

                if (res == -1) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        isDataChanged.setValue(false);
                        setIsLoading(false);
                        callback.onResult(PDFOperationResult.DUPLICATE);
                    });
                    return;
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    isDataChanged.setValue(true);
                    setIsLoading(false);
                    callback.onResult(PDFOperationResult.SUCCESS);
                });
            } catch (Exception e) {
                Log.e("PDF_VM", "Error inserting new PDF", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(PDFOperationResult.ERROR);
                });
            }
        });
    }

    public void removeAllAPdfInCategory(Category category, Callback<PDFOperationResult> callback) {
        removeAllPdfInCategory(category.getId(), callback);
    }

    public void removeAllPdfInCategory(long categoryId, Callback<PDFOperationResult> callback) {
        try {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    var pdfs = this.pdfDAO.findAllByCategoryId(categoryId, sharedPreferencesManager.getUserId());
                    boolean allSuccessful = true;
                    
                    for (var pdf : pdfs) {
                        final AtomicBoolean operationResult = new AtomicBoolean(true);
                        removePDF(pdf, result -> {
                            if (result == PDFOperationResult.ERROR) {
                                operationResult.set(false);
                            }
                        });
                        
                        if (!operationResult.get()) {
                            allSuccessful = false;
                            break;
                        }
                    }

                    final boolean success = allSuccessful;
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onResult(success ? PDFOperationResult.SUCCESS : PDFOperationResult.ERROR);
                    });
                } catch (Exception e) {
                    Log.e("PDF_VM", "Error removing PDFs from category", e);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onResult(PDFOperationResult.ERROR);
                    });
                }
            });
        } catch (Exception e) {
            Log.e("PDF_VM", "Error starting executor", e);
            callback.onResult(PDFOperationResult.ERROR);
        }
    }


    public void removePDF(PDF pdf, Callback<PDFOperationResult> callback) {
        try {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    var success = ThumbnailManager.deleteThumbnail(ctx, pdf.getThumbnailFilePath());

                    if (pdf.getURI().contains("shared_pdfs")) {
                        File file = new File(pdf.getURI());
                        if (file.exists()) {
                            file.delete();
                        }
                    }

                    if (success) {
                        int rowsAffected = pdfDAO.delete(pdf);
                        if (rowsAffected > 0) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                setDataChanged(true);
                                callback.onResult(PDFOperationResult.SUCCESS);
                            });
                            return;
                        }
                    }
                    // All error cases handled on main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onResult(PDFOperationResult.ERROR);
                    });
                } catch (Exception e) {
                    Log.e("PDF_VM", "Error removing PDF", e);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onResult(PDFOperationResult.ERROR);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(PDFOperationResult.ERROR);
        }
    }

    public boolean checkCategoryHasChildPDFs(long categoryId) {
        return this.pdfDAO.hasPDFsInCategory(categoryId, sharedPreferencesManager.getUserId());
    }

    public LiveData<Boolean> isDataChanged() {
        return isDataChanged;
    }

    public void setDataChanged(boolean dataChanged) {
        isDataChanged.setValue(dataChanged);
    }
}
