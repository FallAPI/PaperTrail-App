package com.group2.papertrail.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.dao.PDFDAO;
import com.group2.papertrail.dao.UserDAO;
import com.group2.papertrail.util.ThumbnailManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "papertrail.db";
    private static final int DB_VERSION = 9;
    private static DatabaseManager instance;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Context ctx;


    public DatabaseManager(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.ctx = ctx;
    }

    // PS: syncrhonized keyword is used to allow only one thread to execute this method at any given time
    public static synchronized DatabaseManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseManager(ctx.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryDAO.CREATE_TABLE);
        db.execSQL(PDFDAO.CREATE_TABLE);
        db.execSQL(UserDAO.CREATE_TABLE);
        
        // Add default categories when database is first created
//        addStaticValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PDFDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        onCreate(db);
    }

    // DEBUG
    public void resetDatabaseAndAddStaticValues(Runnable onCompletion) {
        executorService.execute(() -> {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.beginTransaction();

                // Drop all tables
                db.execSQL("DROP TABLE IF EXISTS " + PDFDAO.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + CategoryDAO.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);

                // Delete thumbnails
                var directory = this.ctx.getFilesDir();
                var files = directory.listFiles();

                if (files != null) {
                    for (var file : files) {
                        if (file.isFile()) {
                            var deleted = ThumbnailManager.deleteThumbnail(this.ctx, file.getAbsolutePath());
                            if (!deleted) {
                                Log.e("FileDelete", "Could not delete file: " + file.getName());
                            }
                        }
                    }
                }

                // Recreate tables
                onCreate(db);

                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace(); // Log the error for debugging
            } finally {
                db.endTransaction();
            }

            // Notify the main thread
            if (onCompletion != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(onCompletion);
            }
        });
    }

    private void addStaticValues(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + CategoryDAO.TABLE_NAME + " (name) VALUES ('Personal')");
        db.execSQL("INSERT INTO " + CategoryDAO.TABLE_NAME + " (name) VALUES ('School')");
    }
}
