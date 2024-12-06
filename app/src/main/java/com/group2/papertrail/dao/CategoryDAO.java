package com.group2.papertrail.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.util.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryDAO implements BaseDAO<Category> {

    public static final String TABLE_NAME = "categories";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "user_id INTEGER, " +
                    "UNIQUE(name, user_id)" +
                    ");";

    private final DatabaseManager dbManager;

    public CategoryDAO(Context ctx) {
        this.dbManager = DatabaseManager.getInstance(ctx);
    }


    @Override
    public long insert(Category category) {
        var db = dbManager.getWritableDatabase();
        var values = new ContentValues();
        values.put("name", category.getName());
        values.put("user_id", category.getUserId());

        return db.insert(TABLE_NAME, null, values);
    }

    @Override
    public int update(Category category) {
        var db = dbManager.getWritableDatabase();
        var values = new ContentValues();
        values.put("name", category.getName());
        values.put("user_id", category.getUserId());

        return db.update(TABLE_NAME, values, 
            "id = ? AND user_id = ?", 
            new String[]{String.valueOf(category.getId()), String.valueOf(category.getUserId())});
    }

    @Override
    public int delete(Category category) {
        var db = dbManager.getWritableDatabase();

        return db.delete(TABLE_NAME, 
            "id = ? AND user_id = ?", 
            new String[]{String.valueOf(category.getId()), String.valueOf(category.getUserId())});
    }

    @Override
    public Category findById(long id) {
        SQLiteDatabase db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        Category category = null;
        if (cursor.moveToFirst()) {
            category = new Category(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );
        }
        cursor.close();
        return category;
    }

    @Override
    public List<Category> findAllByUserId(long userId) {
        var categories = new ArrayList<Category>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                "user_id = ? OR user_id IS NULL", new String[]{String.valueOf(userId)},
                null, null, null);

        while (cursor.moveToNext()) {
            var category = new Category(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("user_id"))
            );
            categories.add(category);
        }
        cursor.close();
        return categories;
    }

    public void addDefaultCategories(long userId) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        try {
            db.beginTransaction();
            
            // Add default categories for the user
            ContentValues values = new ContentValues();
            values.put("name", "Personal");
            values.put("user_id", userId);
            db.insert(TABLE_NAME, null, values);
    
            values.clear();
            values.put("name", "School");
            values.put("user_id", userId);
            db.insert(TABLE_NAME, null, values);
    
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
