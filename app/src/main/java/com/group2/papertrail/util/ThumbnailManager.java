package com.group2.papertrail.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

/*
TODO:
  - Generate PDF thumbnail and return URI
  - Load thumbnail
 */
public class ThumbnailManager {
    public static String generateUUID() {
        var uuid = UUID.randomUUID();
        byte[] bytes = uuid.toString().getBytes();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static Bitmap generateThumbnailFromURI(Context ctx, Uri pdfURI) {
        try {
            var fileDescriptor = ctx.getContentResolver().openFileDescriptor(pdfURI, "r");

            if (fileDescriptor == null) {
                return null;
            }

            var pdfRenderer = new PdfRenderer(fileDescriptor);
            var page = pdfRenderer.openPage(0);

            int width = 400;
            int height = (int) (width * ((float) page.getHeight() / page.getWidth()));
            var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(android.graphics.Color.WHITE);

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
            pdfRenderer.close();
            fileDescriptor.close();

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveThumbnailToStorage(Context ctx, Bitmap bitmap) {
        // Generate UUID
        var uuidStr = generateUUID();

        var directory = ctx.getFilesDir();
        var file = new File(directory, uuidStr + ".jpeg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap getPDFThumbnail(Context ctx, String thumbnailURI) {
        var directory = ctx.getFilesDir();
        var file = new File(thumbnailURI);

        FileInputStream fis = null;
        Bitmap bitmap = null;

        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static boolean deleteThumbnail(Context ctx, String thumbnailURI) {
        var uri = Uri.parse(thumbnailURI);
        try {
            // Check if file exists and is deletable
            if (DocumentsContract.isDocumentUri(ctx, uri)) {
                // For document URIs
                var documentFile = DocumentFile.fromSingleUri(ctx, uri);
                if (documentFile != null && documentFile.exists()) {
                    documentFile.delete();
                }
            } else {
                // For regular file URIs
                File file = new File(uri.getPath());
                if (file.exists()) {
                    file.delete();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
