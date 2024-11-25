package com.group2.papertrail.dao;

import android.content.Context;

import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.model.Category;

import java.util.Collections;
import java.util.List;

public class CategoryDAO implements BaseDAO<Category> {

    public static final String TABLE_NAME = "categories";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL " +
                    ");";

    private final DatabaseManager dbManager;

    public CategoryDAO(Context ctx) {
        this.dbManager = DatabaseManager.getInstance(ctx);
    }


    @Override
    public long insert(Category model) {
        return 0;
    }

    @Override
    public int update(Category model) {
        return 0;
    }

    @Override
    public int delete(Category model) {
        return 0;
    }

    @Override
    public Category findById(long id) {
        return null;
    }

    @Override
    public List<Category> findAll() {
        return Collections.emptyList();
    }
}
