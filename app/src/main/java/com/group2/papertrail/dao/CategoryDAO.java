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
                    "name TEXT NOT NULL UNIQUE" +
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

        return db.insert(TABLE_NAME, null, values);
    }

    @Override
    public int update(Category category) {
        var db = dbManager.getWritableDatabase();
        var values = new ContentValues();
        values.put("name", category.getName());

        return db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(category.getId())});
    }

    @Override
    public int delete(Category category) {
        var db = dbManager.getWritableDatabase();

        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(category.getId())});
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
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
        }
        cursor.close();
        return category;
    }

    @Override
    public List<Category> findAll() {
        var categories = new ArrayList<Category>();
        var db = dbManager.getReadableDatabase();
        var cursor = db.query(TABLE_NAME, null,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            var category = new Category(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
            categories.add(category);
        }
        cursor.close();
        return categories;
    }
}
