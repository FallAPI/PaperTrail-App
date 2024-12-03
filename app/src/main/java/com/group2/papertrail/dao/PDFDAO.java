package com.group2.papertrail.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.model.PDF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PDFDAO implements  BaseDAO<PDF> {
    public static final String TABLE_NAME = "pdfs";
    private final DatabaseManager dbManager;
    private final CategoryDAO categoryDAO;

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
                    "created_at INTEGER NOT NULL, " +
                    "updated_at INTEGER, " +
                    "category_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(category_id) REFERENCES categories(id)" +
                    ");";


    public PDFDAO(Context ctx) {
        this.dbManager = DatabaseManager.getInstance(ctx);
        this.categoryDAO = new CategoryDAO(ctx);
    }

    @Override
    public long insert(PDF model) {
        var db = dbManager.getWritableDatabase();
        var values = new ContentValues();
        values.put("file_name", model.getFileName());
        values.put("uri", model.getURI());
        values.put("thumbnail_file_path", model.getThumbnailFilePath());
        values.put("is_favorite", model.isFavorite());
        values.put("title", model.getTitle());
        values.put("author", model.getAuthor());
        values.put("size", model.getSize());
        values.put("page_count", model.getPageCount());
        values.put("created_at", model.getCreatedAt().getTime());
        values.put("category_id", model.getCategory().getId());
        return db.insert(TABLE_NAME, null, values);
    }

    @Override
    public int update(PDF model) {
        var db = dbManager.getWritableDatabase();
        var values = new ContentValues();
        values.put("file_name", model.getFileName());
        values.put("uri", model.getURI());
        values.put("thumbnail_file_path", model.getThumbnailFilePath());
        values.put("is_favorite", model.isFavorite());
        values.put("title", model.getTitle());
        values.put("author", model.getAuthor());
        values.put("size", model.getSize());
        values.put("page_count", model.getPageCount());
        values.put("created_at", model.getCreatedAt().getTime());
        values.put("category_id", model.getCategory().getId());
        return db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public int delete(PDF model) {
        var db = dbManager.getWritableDatabase();

        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(model.getId())});
    }

    @Override
    public PDF findById(long id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        PDF pdf = null;
        if (cursor.moveToFirst()) {
            pdf = new PDF(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("file_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("uri")),
                    cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_file_path")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("size")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("page_count")),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))),
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")))
            );
        }
        cursor.close();
        return pdf;
    }

    public List<PDF> findAllByCategoryId(long categoryId) {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "category_id = ?", new String[]{String.valueOf(categoryId)},
                null, null, null);

        while (cursor.moveToNext()) {
            var pdf = new PDF(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("file_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("uri")),
                    cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_file_path")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("size")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("page_count")),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))),
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }

    public List<PDF> findAllByRangeId(Long[] ids) {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();

        // Create a string with placeholders for each id
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String selection = "id IN (" + placeholders + ")";

        // Convert Long[] to String[]
        String[] selectionArgs = Arrays.stream(ids)
                .map(String::valueOf)
                .toArray(String[]::new);

        var cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            var pdf = new PDF(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("file_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("uri")),
                    cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_file_path")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("size")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("page_count")),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))),
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }

    @Override
    public List<PDF> findAll() {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            var pdf = new PDF(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("file_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("uri")),
                    cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_file_path")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("size")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("page_count")),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))),
                    new Date((long) cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))),
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }
}
