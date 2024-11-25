package com.group2.papertrail.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "papertrail.db";
    private static final int DB_VERSION = 1;
    private static DatabaseManager instance;

    public DatabaseManager(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    // PS: syncrhonized keyword is used to allow only one thread to execute this method at any given time
    public static synchronized DatabaseManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseManager(ctx.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
