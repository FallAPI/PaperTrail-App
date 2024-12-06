package com.group2.papertrail.ui.standalone;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.dao.PDFDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.ui.PDFDataManager;
import com.group2.papertrail.util.Callback;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EditPDFViewModel extends ViewModel {
    private MutableLiveData<String> pdfTitle;
    private MutableLiveData<String> pdfDesc;
    private PDFDataManager pdfDataManager;
    private CategoryDAO categoryDAO;
    private PDFDAO pdfDAO;
    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<Category> selectedCategory;
    private MutableLiveData<Boolean> isLoading;
    private SharedPreferencesManager sharedPreferencesManager;

    public EditPDFViewModel(PDF pdf, Application app) {
        this.pdfDataManager = PDFDataManager.getInstance(app.getApplicationContext());
        pdfTitle = new MutableLiveData<>(pdf.getTitle());
        pdfDesc = new MutableLiveData<>(pdf.getDescription());
        categoryDAO = new CategoryDAO(app.getApplicationContext());
        pdfDAO = new PDFDAO(app.getApplicationContext());
        selectedCategory = new MutableLiveData<>(pdf.getCategory());
        categories = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(app.getApplicationContext());

        loadCategories();
    }

    public enum EditPDFOperations {
        SUCCESS,
        ERROR,

    }

    public MutableLiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory.setValue(selectedCategory);
    }

    public void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Category> categoryList = categoryDAO.findAllByUserId(sharedPreferencesManager.getUserId());
                new Handler(Looper.getMainLooper()).post(() -> {
                    this.categories.setValue(categoryList);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error loading categories", e);
            }
        });
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void selectCategoryById(long id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Category targetCategory = categoryDAO.findById(id);
                new Handler(Looper.getMainLooper()).post(() -> {
                    this.selectedCategory.setValue(targetCategory);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error selecting category", e);
            }
        });

        var targetCategory = categories.getValue().stream().filter(cat -> cat.getId() == id).findFirst().orElse(null);
        this.selectedCategory.setValue(targetCategory);
    }

    public void editPDF(PDF pdf, Callback<EditPDFOperations> callback) {
        setIsLoading(true);

        pdf.setCategory(selectedCategory.getValue());
        pdf.setTitle(pdfTitle.getValue());
        pdf.setDescription(pdfDesc.getValue());
        try {
            Executors.newSingleThreadExecutor().execute(() -> {
                int rowsAffected = pdfDAO.update(pdf);

                if (rowsAffected > 0) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        setIsLoading(false);
                        callback.onResult(EditPDFOperations.SUCCESS);
                        pdfDataManager.setDataChanged(true);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onResult(EditPDFOperations.ERROR);
        }
        setIsLoading(false);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean state) {
        this.isLoading.setValue(state);
    }

    public MutableLiveData<String> getPdfTitle() {
        return pdfTitle;
    }

    public MutableLiveData<String> getPdfDesc() {
        return pdfDesc;
    }

    public void setPdfTitle(String title) {
        pdfTitle.setValue(title);
    }

    public void setPdfDesc(String desc) {
        pdfDesc.setValue(desc);
    }
}
