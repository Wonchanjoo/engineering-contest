package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.example.refrigerator_management_app.databinding.DialogAddCategoryWarningBinding;

public class AddCategoryWarningDialog extends Dialog {
    private DialogAddCategoryWarningBinding dialogAddCategoryWarningBinding;
    private Context context; // 가지고 올 Activity의 Context
    private ImageView warningText; // 다이얼로그의 경고 문구
    private Button okButton;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogAddCategoryWarningBinding = DialogAddCategoryWarningBinding.inflate(getLayoutInflater());
        setContentView(dialogAddCategoryWarningBinding.getRoot());

        /* ---------- 초기화 ---------- */
        warningText = dialogAddCategoryWarningBinding.addCategoryWarningDialogText;
        okButton = dialogAddCategoryWarningBinding.addCategoryWarningDialogOkBtn;

        okButton.setOnClickListener(view -> {
            dismiss();
        });

        switch(mode) {
            // 카테고리 이름이 비어있는 경우
            case 1:
                warningText.setImageResource(R.drawable.no_empty_category);
                Log.e("AddCategoryWarningDialog", "카테고리 이름 비어있음");
                break;
            // 카테고리 이름이 중복되는 경우
            case 2:
                warningText.setImageResource(R.drawable.already_exist_name);
                Log.e("AddCategoryWarningDialog", "카테고리 이름 중복");
                break;
        }
    }

    public AddCategoryWarningDialog(Context context, int mode) {
        super(context);
        this.context = context;
        this.mode = mode;
    }
}
