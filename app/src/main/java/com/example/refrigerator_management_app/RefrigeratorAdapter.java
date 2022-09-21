package com.example.refrigerator_management_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RefrigeratorAdapter extends RecyclerView.Adapter<RefrigeratorAdapter.ViewHolder> {
    private Context context;
    private RefrigeratorViewModel viewModel;
    private String userUid;

    // RefrigeratorAdapter의 생성자
    public RefrigeratorAdapter(Context context, RefrigeratorViewModel viewModel, String userUid){
        this.context = context;
        this.viewModel = viewModel;
        this.userUid = userUid;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView refriPlusImage;
        private TextView refriPlustext; // 뷰홀더의 텍스트 (냉장고의 ID)

        public ViewHolder(@NonNull View view) {
            super(view);
            this.refriPlusImage = view.findViewById(R.id.refri_plusImage);
            this.refriPlustext = view.findViewById(R.id.refri_plustext);

            // 냉장고 클릭시 카테고리 화면으로 이동.
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("userUid", userUid);
                intent.putExtra("refrigeratorID", refriPlustext.getText().toString());
                context.startActivity(intent);
            });

            itemView.setOnCreateContextMenuListener(this); // 냉장고를 롱클릭 했을 때 컨텍스트 메뉴가 보이도록 설정한다.

            // 냉장고를 롱클릭 했을 때 몇 번째 위치에 있는지 알 수 있도록
            // RefrigeratorAdapter의 메소드인 getAdapterPosition을 통해
            // 위치를 알아낸 후 setItemPos를 함.
            itemView.setOnLongClickListener(view1 -> {
                viewModel.setRefrigeratorPos(getAdapterPosition());
                return false;
            });
        }


        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            ((Activity)view.getContext()).getMenuInflater().inflate(R.menu.refrigerator_menu, contextMenu);
        }

        void setContents(int position){
            refriPlustext.setText(viewModel.refrigerators.get(position));
        }
    }

    @NonNull
    @Override
    public RefrigeratorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recyclerview_refrigerator, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RefrigeratorAdapter.ViewHolder viewHolder, int position) {
        viewHolder.setContents(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.getRefrigeratorsSize();
    }

}
