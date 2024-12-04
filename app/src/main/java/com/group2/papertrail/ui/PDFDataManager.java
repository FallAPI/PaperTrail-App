package com.group2.papertrail.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.group2.papertrail.util.ThumbnailManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class PDFDataManager {
    private static PDFDataManager instance;
    private MutableLiveData<List<PDF>> pdfFiles;

    private final MutableLiveData<Boolean> isDataChanged = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final Context ctx;

    private PDFDAO pdfDAO;

    public enum PDFOperationResult {
        SUCCESS,
        ERROR,
        EMPTY_FILE
    }

    private PDFDataManager(Context context) {
        this.pdfFiles = new MutableLiveData<>(new ArrayList<PDF>());
        this.ctx = context.getApplicationContext();
        this.pdfDAO = new PDFDAO(this.ctx);
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

    public void loadPDF(Long[] ids, Callback<PDFOperationResult> callback) {
        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<PDF> pdfs = pdfDAO.findAllByRangeId(ids);
                new Handler(Looper.getMainLooper()).post(() -> {
                    pdfFiles.setValue(pdfs);
                    setIsLoading(false);
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

    public void loadPDF(long categoryId, Callback<PDFOperationResult> callback) {
        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<PDF> pdfs = pdfDAO.findAllByCategoryId(categoryId);
                new Handler(Looper.getMainLooper()).post(() -> {
                    pdfFiles.setValue(pdfs);
                    setIsLoading(false);
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

    public void loadPDF(Callback<PDFOperationResult> callback) {
        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<PDF> pdfs = pdfDAO.findAll();
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

    public void addPDF(FilePicker.FileMetadata metadata, Category category, Callback<PDFOperationResult> callback) {
        if (metadata == null || category == null) {
            callback.onResult(PDFOperationResult.EMPTY_FILE);
            return;
        }

        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                var thumbnailBitmap = ThumbnailManager.generateThumbnailFromURI(this.ctx, metadata.getFileUri());
                var thumbnailURI = ThumbnailManager.saveThumbnailToStorage(this.ctx, thumbnailBitmap);
                var pdfMetadata = PDFManager.readPDFMetadata(this.ctx, metadata.getFileUri());

                var newPdf = new PDF(
                        metadata.getFileName(),
                        metadata.getFileUri().toString(),
                        thumbnailURI,
                        pdfMetadata.getTitle(),
                        pdfMetadata.getAuthor(),
                        metadata.getFileSize(),
                        pdfMetadata.getPageCount(),
                        pdfMetadata.getCreationDate() != null ? pdfMetadata.getCreationDate() : new Date(),
                        category,
                        pdfMetadata.getCreationDate() != null
                );

                pdfDAO.insert(newPdf);
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

    public void removePDF(PDF pdf, Callback<PDFOperationResult> callback) {
        try {
            Executors.newSingleThreadExecutor().execute(() -> {

                var success = ThumbnailManager.deleteThumbnail(ctx, pdf.getThumbnailFilePath());
                if (success) {
                    int rowsAffected = pdfDAO.delete(pdf);
                    if (rowsAffected > 0) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            setDataChanged(true);
                            callback.onResult(PDFOperationResult.SUCCESS);
                        });
                    }
                } else {
                    callback.onResult(PDFOperationResult.ERROR);
                }
            });
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        callback.onResult(PDFOperationResult.ERROR);
    }

    public LiveData<Boolean> isDataChanged() {
        return isDataChanged;
    }

    public void setDataChanged(boolean dataChanged) {
        isDataChanged.setValue(dataChanged);
    }
}
