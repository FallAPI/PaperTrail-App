package com.group2.papertrail.dao;

import com.group2.papertrail.model.User;

import java.util.Collections;
import java.util.List;

public class UserDAO implements BaseDAO<User> {
    public static final String TABLE_NAME = "User";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
            "userId INTEGER PRIMARY KEY AUTO INCREMENT, " +
            "username TEXT NOT NULL, " +
            "password TEXT NOT NULL " +
            ");";


    @Override
    public long insert(User model) {
        return 0;
    }

    @Override
    public int update(User model) {
        return 0;
    }

    @Override
    public int delete(User model) {
        return 0;
    }

    @Override
    public User findById(long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
