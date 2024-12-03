package com.group2.papertrail.ui.standalone;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityPdfdetailBinding;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.util.ThumbnailManager;

import java.text.StringCharacterIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PDFDetailActivity extends AppCompatActivity {

    private ActivityPdfdetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPdfdetailBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.myToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        var intent = getIntent();
        var pdf = (PDF) intent.getParcelableExtra("pdf");

        populateFields(pdf);
    }

    private void populateFields(PDF pdf) {
        binding.titleTextView.setText(pdf.getTitle() != null ? pdf.getTitle() : "-");
        binding.authorTextView.setText(pdf.getAuthor() != null ? pdf.getAuthor() : "-");
        binding.descriptionTextView.setText(pdf.getDescription() != null ? pdf.getDescription() : "-");
        binding.filenameTextView.setText(pdf.getFileName() != null ? pdf.getFileName() : "-");
        binding.sizeTextView.setText(humanReadableByteCountSI(pdf.getSize()));
        binding.pageCountTextView.setText(String.valueOf(pdf.getPageCount()));
        binding.dateCreatedTextView.setText(pdf.getCreatedAt() != null ? pdf.getCreatedAt().toString() : "-");
        binding.dateUpdatedTextView.setText(pdf.getUpdatedAt() != null && pdf.getUpdatedAt().getTime() != 0 ? pdf.getUpdatedAt().toString() : "-");
        binding.categoryTextView.setText(pdf.getCategory() != null ? pdf.getCategory().getName() : "-");

        Executors.newSingleThreadExecutor().execute(() -> {
            var bitmap = ThumbnailManager.getPDFThumbnail(this, pdf.getThumbnailFilePath());
            if (bitmap != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                   binding.detailImg.setImageBitmap(bitmap);
                });
            }
        });
    }

    private String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        var ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

}