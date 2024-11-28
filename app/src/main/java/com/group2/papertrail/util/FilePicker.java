package com.group2.papertrail.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;

public class FilePicker {
    private static final int FILE_PICK_REQUEST_CODE = 120;
    private Activity activity;

    public FilePicker(Activity activity) {
        this.activity = activity;
    }

    public void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");  // Allow all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        activity.startActivityForResult(intent, FILE_PICK_REQUEST_CODE);
    }

    public FileMetadata getFileMetadata(Uri fileUri) {
        String fileName = null;
        long fileSize = -1;

        Cursor cursor = activity.getContentResolver()
                .query(fileUri, null, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                fileName = cursor.getString(nameIndex);
                fileSize = cursor.getLong(sizeIndex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return new FileMetadata(fileName, fileUri, fileSize);
    }

    public static class FileMetadata {
        private String fileName;
        private Uri fileUri;
        private long fileSize;

        public FileMetadata(String fileName, Uri fileUri, long fileSize) {
            this.fileName = fileName;
            this.fileUri = fileUri;
            this.fileSize = fileSize;
        }

        // Getters
        public String getFileName() { return fileName; }
        public Uri getFileUri() { return fileUri; }
        public long getFileSize() { return fileSize; }
    }

    // Handle in onActivityResult of your Activity
    public FileMetadata handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedFileUri = data.getData();

                activity.getContentResolver().takePersistableUriPermission(selectedFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                FileMetadata metadata = getFileMetadata(selectedFileUri);

                return metadata;
            }
        }
        return null;
    }
}
