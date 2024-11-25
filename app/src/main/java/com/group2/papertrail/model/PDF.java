package com.group2.papertrail.model;

import java.util.Date;

public class PDF {
    private long id;
    private String fileName;
    private String description;
    private String URI;
    private String thumbnailFilePath;
    private boolean isFavorite = false;
    private String title;
    private String author;
    private long size;
    private int pageCount;
    private Date createdAt;
    private Date updatedAt;
    private Category category;

    public PDF(long id, String fileName, String description, String URI, String thumbnailFilePath, boolean isFavorite, String title, String author, long size, int pageCount, Date createdAt, Date updatedAt, Category category) {
        this.id = id;
        this.fileName = fileName;
        this.description = description;
        this.URI = URI;
        this.thumbnailFilePath = thumbnailFilePath;
        this.isFavorite = isFavorite;
        this.title = title;
        this.author = author;
        this.size = size;
        this.pageCount = pageCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
    }

    public PDF(String fileName, String URI, Category category, long size, Date createdAt) {
        this.fileName = fileName;
        this.URI = URI;
        this.category = category;
        this.size = size;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }

    public void setThumbnailFilePath(String thumbnailFilePath) {
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
