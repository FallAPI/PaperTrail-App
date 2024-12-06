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
                    "is_original_date BOOLEAN DEFAULT 1, " +
                    "updated_at INTEGER, " +
                    "category_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(category_id) REFERENCES categories(id), " +
                    "FOREIGN KEY(user_id) REFERENCES users(id)" +
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

        if (model.isOriginalDate()) {
            values.put("created_at", model.getCreatedAt().getTime());
        } else {
            values.put("created_at", new Date().getTime());
            values.put("is_original_date", false);
        }

        values.put("category_id", model.getCategory().getId());
        values.put("user_id", model.getUserId());
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
        values.put("description", model.getDescription());
        values.put("author", model.getAuthor());
        values.put("size", model.getSize());
        values.put("page_count", model.getPageCount());
        values.put("created_at", model.getCreatedAt().getTime());
        values.put("updated_at", new Date().getTime());
        values.put("category_id", model.getCategory().getId());
        values.put("user_id", model.getUserId());
        return db.update(TABLE_NAME, values, "id = ? AND user_id = ?", 
            new String[]{String.valueOf(model.getId()), String.valueOf(model.getUserId())});
    }

    @Override
    public int delete(PDF model) {
        var db = dbManager.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ? AND user_id = ?", 
            new String[]{String.valueOf(model.getId()), String.valueOf(model.getUserId())});
    }

    @Override
    public PDF findById(long id) {
        return null;
    }


    public PDF findById(long id, long userId) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "id = ? AND user_id = ?", new String[]{String.valueOf(id), String.valueOf(userId)},
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
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_original_date")) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );
        }
        cursor.close();
        return pdf;
    }

    public List<PDF> findAllByCategoryId(long categoryId, long userId) {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "category_id = ? AND user_id = ?", 
                new String[]{String.valueOf(categoryId), String.valueOf(userId)},
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
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_original_date")) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }

    public List<PDF> findAllByRangeId(Long[] ids, long userId) {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();

        // Create a string with placeholders for each id
        String placeholders = String.join(",", Collections.nCopies(ids.length, "?"));
        String selection = "id IN (" + placeholders + ") AND user_id = ?";
        
        // Add userId to selection args
        String[] selectionArgs = Arrays.stream(ids)
                .map(String::valueOf)
                .toArray(String[]::new);
        String[] finalArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
        finalArgs[finalArgs.length - 1] = String.valueOf(userId);
        
        var cursor = db.query(TABLE_NAME, null, selection, finalArgs, null, null, null);

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
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_original_date")) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }


    @Override
    public List<PDF> findAllByUserId(long userId) {
        var pdfs = new ArrayList<PDF>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "user_id = ?", new String[]{String.valueOf(userId)},
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
                    categoryDAO.findById(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_original_date")) == 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );

            pdfs.add(pdf);
        }
        cursor.close();
        return pdfs;
    }
}
