package com.group2.papertrail.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.papertrail.R;
import java.util.List;

public class PaperRecylerViewAdapter extends RecyclerView.Adapter<PaperRecylerViewAdapter.viewHolder> {
    private List<String> items;

    public PaperRecylerViewAdapter(List<String> items){
        this.items = items;
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        public viewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.item_title);
        }

    }
}
