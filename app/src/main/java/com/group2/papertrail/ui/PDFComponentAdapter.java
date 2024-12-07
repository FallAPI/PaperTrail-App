package com.group2.papertrail.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.view.menu.MenuPopupHelper;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.group2.papertrail.R;
import com.group2.papertrail.dao.PDFDAO;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.ui.home.HomeViewModel;
import com.group2.papertrail.ui.standalone.EditPDFActivity;
import com.group2.papertrail.ui.standalone.PDFDetailActivity;
import com.group2.papertrail.util.RecentlyViewedUtil;
import com.group2.papertrail.util.SharedPreferencesManager;
import com.group2.papertrail.util.ThumbnailManager;
import com.rajat.pdfviewer.PdfViewerActivity;
import com.rajat.pdfviewer.util.saveTo;

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

        holder.actionButton.setOnClickListener(view -> {
            if (view.getId() == R.id.item_menu){
                showPopupMenu(view, position);
            }
        });

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

        holder.itemView.setOnClickListener(v -> {
            Log.d("LAUNCH_PDF", "ItemView clicked");
            var pdfIntent = PdfViewerActivity.Companion.launchPdfFromPath(
                    holder.itemView.getContext(),
                    item.getPdf().getURI(),
                    !item.getPdf().getTitle().isEmpty() ? item.getPdf().getTitle() : item.getPdf().getFileName(),
                    saveTo.ASK_EVERYTIME,
                    false
            );

            // Update recently viewed PDFs through HomeViewModel
            HomeViewModel.updateRecentlyViewedPDF(holder.itemView.getContext(), item.getPdf().getId());

            holder.itemView.getContext().startActivity(pdfIntent);
        });
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


    private void showPopupMenu(View view, int position){
        var item = items.get(position);
        androidx.appcompat.widget.PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_actions, popupMenu.getMenu());

        MenuItem favoriteMenuItem = popupMenu.getMenu().findItem(R.id.action_favorite);
        boolean isFavorite = item.getPdf().isFavorite();
        favoriteMenuItem.setTitle(isFavorite ? "Unfavorite" : "Favorite");
        favoriteMenuItem.setIcon(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star);

        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_detail) {
                // Handle "Details" action
                var intent = new Intent(view.getContext(), PDFDetailActivity.class);
                intent.putExtra("pdf", item.getPdf());
                view.getContext().startActivity(intent);
            } else if (menuItem.getItemId() == R.id.action_edit) {
                // Handle "Edit" action
                var intent = new Intent(view.getContext(), EditPDFActivity.class);
                intent.putExtra("pdf", item.getPdf());
                view.getContext().startActivity(intent);
            } else if (menuItem.getItemId() == R.id.action_remove) {
                // Handle "Remove" action
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Remove PDF")
                        .setMessage("Are you sure you want to remove this PDF?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            try {
//                                PDFDAO pdfDAO = new PDFDAO(view.getContext());
//                                int deletedRows = pdfDAO.delete(item.getPdf());

                                PDFDataManager.getInstance(view.getContext().getApplicationContext()).removePDF(item.getPdf(), result -> {
                                    switch (result) {
                                        case SUCCESS:
                                            items.remove(position);
                                            notifyItemRemoved(position);
                                            break;
                                        case ERROR:
                                            Toast.makeText(view.getContext(), "Succesfully to detele PDF", Toast.LENGTH_SHORT).show();
                                    }
                                });

//                                if (deletedRows > 0) {
//                                    if (view.getContext() instanceof Activity) {
//                                        ((Activity) view.getContext()).runOnUiThread(() -> {
//                                        });
//                                    }
//                                }
                            } catch (Exception e) {
                                Log.e("DeleteError", "Error deleting PDF", e);
                                // Optional: show user-friendly error toast
                                Toast.makeText(view.getContext(), "Failed to delete PDF", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            } else if (menuItem.getItemId() == R.id.action_favorite) {
                // Handle "Favorite" action
                PDF currentPDF = item.getPdf();
                boolean newFavoriteStatus = !currentPDF.isFavorite();
                currentPDF.setFavorite(newFavoriteStatus);

                PDFDAO pdfdao = new PDFDAO(view.getContext());
                pdfdao.update(currentPDF);

                items.set(position, item);
                notifyItemChanged(position);

                PDFDataManager.getInstance(view.getContext()).setDataChanged(true);

                Toast.makeText(view.getContext(), newFavoriteStatus ? "Added to Favorites" : "Removed from Favorites" , Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    public void updateData(List<PDFComponent> pdfComponents) {
        this.items = pdfComponents;
        notifyDataSetChanged();
    }
}
