package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.refrigerator_management_app.databinding.DialogAddCategoryBinding;

public class AddCategoryDialog extends Dialog {
    private DialogAddCategoryBinding dialogAddCategoryBinding;
    private CategoryViewModel viewModel;
    private Button okButton;
    private Button cancelButton;
    private AddCategoryWarningDialog warningDialog;
    private Context context;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogAddCategoryBinding = DialogAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(dialogAddCategoryBinding.getRoot());

        okButton = dialogAddCategoryBinding.addCategoryDialogButtonOk;
        cancelButton = dialogAddCategoryBinding.addCategoryDialogButtonCancel;

        okButton.setOnClickListener(view -> {
            String category = dialogAddCategoryBinding.addCategoryDialogEditText.getText().toString(); // 입력한 카테고리 이름

            // 입력된 카테고리 이름이 비어있는 경우
            if(category.equals("")) {
                warningDialog = new AddCategoryWarningDialog(context, 1);
                warningDialog.show();
                return;
            }
            // 냉장 카테고리 안에 이미 존재하는 경우
            if(type.equals("냉장") && viewModel.refrigerationCategorys.contains(category)) {
                warningDialog = new AddCategoryWarningDialog(context, 2);
                warningDialog.show();
                return;
            }
            // 냉동 카테고리 안에 이미 존재하는 경우
            if (type.equals("냉동") && viewModel.freezeCategorys.contains(category)) {
                warningDialog = new AddCategoryWarningDialog(context, 2);
                warningDialog.show();
                return;
            }

            // 카테고리 추가
            if(type.equals("냉장"))
                viewModel.addRefrigerationCategory(category);
            else
                viewModel.addFreezeCategory(category);
            Log.e("AddCategoryDialog", category + " 추가");

            dismiss();
        });

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });
    }

    public AddCategoryDialog(Context context,CategoryViewModel viewModel, String type) {
        super(context);
        this.context = context;
        this.viewModel = viewModel;
        this.type = type;
    }
}
