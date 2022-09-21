package com.example.refrigerator_management_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ItemViewModel viewModel;

    public ItemAdapter(ItemViewModel viewModel) {
        this.viewModel = viewModel;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemTextView = itemView.findViewById(R.id.itemTextView);;

            this.itemTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    viewModel.longClickPosition = getAdapterPosition();
                    return false;
                }
            });
        }

        public void setContents(int pos) {
            Item item = viewModel.items.get(pos);
            itemTextView.setText(item.getName());
        }
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setContents(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.getItemSize();
    }
}
