package com.group2.papertrail.model;

import java.util.Date;

public class PDFAdditionalMetadata {
    private String title;
    private String author;
    private Date creationDate;
    private Date modificationDate;
    private int pageCount;

    public PDFAdditionalMetadata(String title, String author, Date creationDate, Date modificationDate, int pageCount) {
        this.title = title;
        this.author = author;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.pageCount = pageCount;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
