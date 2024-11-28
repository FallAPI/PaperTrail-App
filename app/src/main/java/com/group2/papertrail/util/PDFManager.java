package com.group2.papertrail.util;

import android.content.Context;
import android.net.Uri;

import com.group2.papertrail.model.PDFAdditionalMetadata;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.io.IOUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public class PDFManager {
    public static PDFAdditionalMetadata readPDFMetadata(Context ctx, Uri pdfURI) {
        PDFBoxResourceLoader.init(ctx);
        try {
            // Load the PDF document
            var inputStream = ctx.getContentResolver().openInputStream(pdfURI);
            var document = PDDocument.load(inputStream);


            // Get metadata with null checks
            var info = document.getDocumentInformation();
            var title = info.getTitle() != null ? info.getTitle() : "";
            var author = info.getAuthor() != null ? info.getAuthor() : "";
            var creationDate = info.getCreationDate() != null ? info.getCreationDate().getTime() : null;
            var modificationDate = info.getModificationDate() != null ? info.getModificationDate().getTime() : null;

            var pdfMetadata = new PDFAdditionalMetadata(title, author, creationDate, modificationDate, document.getNumberOfPages());

            // Print metadata with null checks
            System.out.println("Title: " + (title.isEmpty() ? "N/A" : title));
            System.out.println("Author: " + (author.isEmpty() ? "N/A" : author));

            // Close the document
            document.close();

             return pdfMetadata;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
