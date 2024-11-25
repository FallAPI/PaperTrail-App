package com.group2.papertrail.dao;

import android.content.Context;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.model.PDF;

import java.util.Collections;
import java.util.List;

public class PDFDAO implements  BaseDAO<PDF> {
    public static final String TABLE_NAME = "pdfs";
    private final DatabaseManager dbManager;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "file_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "uri TEXT NOT NULL UNIQUE, " +
                    "thumbnail_file_path TEXT, " +
                    "is_favorite BOOLEAN DEFAULT 0, " +
                    "title TEXT, " +
                    "author TEXT, " +
                    "size INTEGER NOT NULL, " +
                    "page_count INTEGER NOT NULL, " +
                    "created_at DATETIME NOT NULL, " +
                    "updated_at DATETIME, " +
                    "category_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(category_id) REFERENCES categories(id)" +
                    ");";


    public PDFDAO(Context ctx) {
        this.dbManager = DatabaseManager.getInstance(ctx);
    }

    @Override
    public long insert(PDF model) {
        return 0;
    }

    @Override
    public int update(PDF model) {
        return 0;
    }

    @Override
    public int delete(PDF model) {
        return 0;
    }

    @Override
    public PDF findById(long id) {
        return null;
    }

    @Override
    public List<PDF> findAll() {
        return Collections.emptyList();
    }
}
