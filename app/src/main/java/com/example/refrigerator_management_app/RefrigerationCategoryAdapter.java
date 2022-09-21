package com.example.refrigerator_management_app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RefrigerationCategoryAdapter extends RecyclerView.Adapter<RefrigerationCategoryAdapter.ViewHolder> {
    private CategoryViewModel viewModel;
    private Context context;

    public RefrigerationCategoryAdapter(CategoryViewModel viewModel, Context context) {
        this.viewModel = viewModel;
        this.context = context;
    }

    // ViewHolder Class - 아이템 뷰를 저장
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.category = itemView.findViewById(R.id.category);
            this.category.setOnClickListener(view -> {
                TextView textView = (TextView) view;

                Intent intent = new Intent(context, ItemActivity.class);
                intent.putExtra("userUid", viewModel.getUserUid());
                intent.putExtra("ID", viewModel.getRefrigeratorId());
                intent.putExtra("category", (String)textView.getText()); // intent에 클릭한 카테고리 이름 넣어주기
                intent.putExtra("type", "냉장");

                context.startActivity(intent); // 액티비티 이동
            });
            this.category.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("Category ViewHolder", "Long Click Position = " + getAdapterPosition());
                    viewModel.longClickPosition = getAdapterPosition();
                    return false;
                }
            });

        }

        public void setContents(int pos) {
            String text = viewModel.refrigerationCategorys.get(pos);
            category.setText(text);
        }
    }

    @NonNull
    @Override
    public RefrigerationCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recyclerview_category, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = viewModel.refrigerationCategorys.get(position);
        holder.setContents(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.getRefrigerationCategorySize();
    }
}
