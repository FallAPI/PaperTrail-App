package com.group2.papertrail.ui;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.papertrail.R;
import com.group2.papertrail.util.ThumbnailManager;

import java.util.List;
import java.util.concurrent.Executors;

public class PDFComponentAdapter extends RecyclerView.Adapter<PDFComponentAdapter.PDFViewHolder> {
    private List<PDFComponent> items;

    public PDFComponentAdapter(List<PDFComponent> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf, parent, false);
        return new PDFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder holder, int position) {
        var item = items.get(position);

        holder.titleTextView.setText(!item.getPdf().getTitle().isEmpty() ? item.getPdf().getTitle() : item.getPdf().getFileName());

        if (item.getPdf().getThumbnailFilePath() != null) {
            if (!item.isLoading()) {
                // Start loading thumbnail
                item.setLoading(true);
                holder.thumbnailImageView.setVisibility(View.INVISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);

                Executors.newSingleThreadExecutor().execute(() -> {
                    var thumbnailURI = item.getPdf().getThumbnailFilePath();
                    var thumbnailBitmap = ThumbnailManager.getPDFThumbnail(holder.itemView.getContext(), thumbnailURI);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.thumbnailImageView.setVisibility(View.VISIBLE);

                        if (thumbnailBitmap != null) {
                            holder.thumbnailImageView.setImageBitmap(thumbnailBitmap);
                        } else {
                            holder.thumbnailImageView.setImageResource(R.drawable.ic_description);
                        }

                        item.setLoading(false);
                    });
                });
            } else if (!item.isLoading() && holder.thumbnailImageView.getDrawable() != null) {
                // Thumbnail already loaded
                holder.progressBar.setVisibility(View.GONE);
                holder.thumbnailImageView.setVisibility(View.VISIBLE);
            }
        } else {
            // No thumbnail path available
            holder.progressBar.setVisibility(View.GONE);
            holder.thumbnailImageView.setVisibility(View.VISIBLE);
            holder.thumbnailImageView.setImageResource(R.drawable.ic_description);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }


    public static class PDFViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView thumbnailImageView;
        ProgressBar progressBar;
        Button actionButton;

        public PDFViewHolder(@NonNull View pdfView) {
            super(pdfView);
            titleTextView = pdfView.findViewById(R.id.item_title);
            thumbnailImageView = pdfView.findViewById(R.id.item_image);
            progressBar = pdfView.findViewById(R.id.item_progress_circular);
            actionButton = pdfView.findViewById(R.id.item_menu);
        }
    }
}
