package com.group2.papertrail.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class PDF implements Parcelable {
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
    private boolean isOriginalDate;

    public PDF(long id, String fileName, String description, String URI, String thumbnailFilePath, boolean isFavorite, String title, String author, long size, int pageCount, Date createdAt, Date updatedAt, Category category, boolean isOriginalDate) {
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
        this.isOriginalDate = isOriginalDate;
    }

    public PDF(String fileName, String URI, Category category, long size, Date createdAt, boolean isOriginalDate) {
        this.fileName = fileName;
        this.URI = URI;
        this.category = category;
        this.size = size;
        this.createdAt = createdAt;
        this.isOriginalDate = isOriginalDate;
    }

    public PDF(String fileName, String URI, String thumbnailFilePath, String title, String author, long size, int pageCount, Date createdAt, Category category, boolean isOriginalDate) {
        this.fileName = fileName;
        this.URI = URI;
        this.thumbnailFilePath = thumbnailFilePath;
        this.title = title;
        this.author = author;
        this.size = size;
        this.pageCount = pageCount;
        this.category = category;
        this.createdAt = createdAt;
        this.isOriginalDate = isOriginalDate;
    }

    protected PDF(Parcel in) {
        id = in.readLong();
        fileName = in.readString();
        description = in.readString();
        URI = in.readString();
        thumbnailFilePath = in.readString();
        isFavorite = in.readByte() != 0;
        title = in.readString();
        author = in.readString();
        size = in.readLong();
        pageCount = in.readInt();
        createdAt = new Date(in.readLong());
        updatedAt = new Date(in.readLong());
        category = in.readParcelable(Category.class.getClassLoader());
        isOriginalDate = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(fileName);
        dest.writeString(description);
        dest.writeString(URI);
        dest.writeString(thumbnailFilePath);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(title);
        dest.writeString(author);
        dest.writeLong(size);
        dest.writeInt(pageCount);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
        dest.writeParcelable(category, flags);
        dest.writeByte((byte) (isOriginalDate ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PDF> CREATOR = new Creator<PDF>() {
        @Override
        public PDF createFromParcel(Parcel in) {
            return new PDF(in);
        }

        @Override
        public PDF[] newArray(int size) {
            return new PDF[size];
        }
    };

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

    public boolean isOriginalDate() {
        return isOriginalDate;
    }

    public void setOriginalDate(boolean originalDate) {
        isOriginalDate = originalDate;
    }
}
