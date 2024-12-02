package com.group2.papertrail.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.group2.papertrail.R;
import java.util.List;

public class PaperRecylerViewAdapter extends RecyclerView.Adapter<PaperRecylerViewAdapter.viewHolder> {
    private List<String> items;
    private Context context;

    public PaperRecylerViewAdapter(List<String> items, Context context){
        this.items = items;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.titleTextView.setText(items.get(position));

        holder.menuButton.setOnClickListener(view -> {
            if (view.getId() == R.id.item_menu){
                showPopupMenu(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        MaterialButton menuButton;

        public viewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
            menuButton = itemView.findViewById(R.id.item_menu);
        }

    }

    private void showPopupMenu(View view, int position){
        androidx.appcompat.widget.PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_actions, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_detail) {
                // Handle "Details" action

            } else if (menuItem.getItemId() == R.id.action_edit) {
                // Handle "Edit" action

            } else if (menuItem.getItemId() == R.id.action_remove) {
                // Handle "Remove" action

            } else if (menuItem.getItemId() == R.id.action_favorite) {
                // Handle "Favorite" action

            }
            return true;
        });

        popupMenu.show();
    }

}
