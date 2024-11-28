package com.group2.papertrail.ui;

import com.group2.papertrail.model.PDF;

public class PDFComponent {
    private PDF pdf;
    private boolean isLoading = false;

    public PDFComponent(PDF pdf) {
        this.pdf = pdf;
    }

    public PDF getPdf() {
        return pdf;
    }

    public void setPdf(PDF pdf) {
        this.pdf = pdf;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
