package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.refrigerator_management_app.databinding.DialogAddItemBinding;

import java.util.ArrayList;

public class AddItemDialog extends Dialog {
    private DialogAddItemBinding dialogAddItemBinding;
    private ItemViewModel viewModel;
    private ArrayList<Item> allItems;
    private Button okButton;
    private Button cancelButton;
    private Context context;
    private AddItemWarningDialog warningDialog;

    /* ---------- 전달된 item 정보들  ---------- */
    private String itemName; // 이름
    private String expirationDate; // 유통기한
    private String memo; // 메모

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogAddItemBinding = DialogAddItemBinding.inflate(getLayoutInflater());
        setContentView(dialogAddItemBinding.getRoot());

        dialogAddItemBinding.addItemDialogNameTextView.setText(itemName);

        okButton = dialogAddItemBinding.addItemDialogButtonOk;
        cancelButton = dialogAddItemBinding.addItemDialogButtonCancel;

        // OK 버튼 -> 추가
        okButton.setOnClickListener(view -> {
            // 입력된 item 이름이 비어있는 경우
            if(itemName.equals("")) {
                dismiss();
                warningDialog = new AddItemWarningDialog(context, 1);
                warningDialog.show();
                return;
            }
            // 유통기한이 비어있는 경우
            if(expirationDate.equals("")) {
                dismiss();
                warningDialog = new AddItemWarningDialog(context, 2);
                warningDialog.show();
                return;
            }

            Item item = new Item(itemName, expirationDate, memo); // 전달된 정보들로 item 객체 생성하고
            // 카테고리 안에 같은 이름이 존재하는 경우
            if(viewModel.items.contains(item)) {
                dismiss();
                warningDialog = new AddItemWarningDialog(context, 3);
                warningDialog.show();
                return;
            }

            // 유통기한 형식이 맞지 않는 경우
            if(!isCorrectExpirationDate(expirationDate)) {
                dismiss();
                warningDialog = new AddItemWarningDialog(context, 4);
                warningDialog.show();
                return;
            }

            viewModel.addItem(item); // 뷰모델에 추가
            if(!allItems.contains(item))
                allItems.add(item); // array list에도 추가
            Log.e("AddItemDialog", itemName + ", " + expirationDate + " 추가");

            Intent intent = new Intent(context, ItemActivity.class);
            context.startActivity(intent);
        });

        // CANCEL 버튼 -> 추가하지않고 다이얼로그 닫기기
        cancelButton.setOnClickListener(view -> {
            dismiss();
        });
    }

    public AddItemDialog(Context context, ItemViewModel viewModel, ArrayList<Item> allItems, String itemName, String expirationDate, String memo) {
        super(context);
        this.context = context;
        this.viewModel = viewModel;
        this.allItems = allItems;
        this.itemName = itemName;
        this.expirationDate = expirationDate;
        this.memo = memo;
    }

    public boolean isCorrectExpirationDate(String date) {
        // "yyyy-mm-dd"
        // 유통기한 문자열의 길이가 4 + 1 + 2 + 1 + 2 = 10이 아니면 false
        if(date.length() != 10)
            return false;

        String year1 = date.substring(0, 4);
        int year2 = Integer.parseInt(year1);
        // 년도가 2022보다 작은 경우
        if(year2 < 2022)
            return false;

        String dash1 = date.substring(4, 5); // "-" 1
        // "-"가 아닌 경우
        if(!dash1.equals("-"))
            return false;

        String month1 = date.substring(5, 7);
        int month2 = Integer.parseInt(month1);
        // 월이 1보다 작거나 12보다 큰 경우
        if(month2 < 1 || month2 > 12)
            return false;

        String dash2 = date.substring(7, 8); // "-" 2
        // "-"가 아닌 경우
        if(!dash2.equals("-"))
            return false;

        String day1 = date.substring(8, 10);
        int day2 = Integer.parseInt(day1);
        // 일이 1보다 작거나 31보다 큰 경우
        if(day2 < 1 || day2 > 31)
            return false;

        return true;
    }
}
