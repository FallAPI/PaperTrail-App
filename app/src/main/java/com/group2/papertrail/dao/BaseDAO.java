package com.group2.papertrail.dao;

import java.util.List;

public interface BaseDAO<T> {
    long insert(T model);
    int update(T model);
    int delete(T model);
    T findById(long id);
    List<T> findAllByUserId(long userId);
}
