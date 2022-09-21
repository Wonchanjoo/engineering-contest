package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.refrigerator_management_app.databinding.DialogAddItemWarningBinding;

public class AddItemWarningDialog extends Dialog {
    private DialogAddItemWarningBinding dialogAddItemWarningBinding;
    private Context context;
    private ImageView warningText;
    private Button okButton;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogAddItemWarningBinding = DialogAddItemWarningBinding.inflate(getLayoutInflater());
        setContentView(dialogAddItemWarningBinding.getRoot());

        /* ---------- 초기화 ---------- */
        warningText = dialogAddItemWarningBinding.addItemWarningDialogText;
        okButton = dialogAddItemWarningBinding.addItemWarningDialogOkBtn;

        okButton.setOnClickListener(view ->  {
            dismiss();
        });

        switch (mode) {
            // 아이템 이름이 비어있는 경우
            case 1:
                warningText.setImageResource(R.drawable.no_empty_item_name);
                break;
            // 유통기한이 비어있는 경우
            case 2:
                warningText.setImageResource(R.drawable.no_empty_item_expiration_date);
                break;
            // 아이템 이름이 카테고리 내에서 중복되는 경우
            case 3:
                warningText.setImageResource(R.drawable.already_exist_name);
                break;
            // 유통기한 형식이 맞지 않는 경우
            case 4:
                warningText.setImageResource(R.drawable.uncorrect_expiration_date);
                break;
        }

    }
    public AddItemWarningDialog(Context context, int mode) {
        super(context);
        this.context = context;
        this.mode = mode;
    }
}
