package com.example.refrigerator_management_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemActivity extends AppCompatActivity {
    private final int itemFragment = 1;
    private final int itemInformationFragment = 2;
    private final int addItemFragment = 3;
    private final int calendarFragment = 4;
    public int currentFragment = itemFragment;

    private String userUid;
    private String refrigeratorId; // 냉장고 이름
    private String category; // 카테고리 이름
    private String type;
    private String clickItemName; // 클릭한 item 이름
    private AddItemDialog addItemDialog;
    private TextView selectedCategoryTextView;

    private Uri uri;
    private Bitmap bitmap;
    private InputImage inputImage;

    private ItemViewModel viewModel;
    private ArrayList<Item> allItems;

    private SharedPreferences itemPreferences;
    private String SharedPrefFile = "com.example.android.MyApplication3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Log.e("ItemActivity", "onCreate()");

        /* ---------- Intent 받아와서 uid, id, category 초기화 ---------- */
        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid");
        refrigeratorId = intent.getStringExtra("ID");
        category = intent.getStringExtra("category");
        type = intent.getStringExtra("type");

        /* ---------- uid, id, category, type이 null이면 저장소에서 값 가져오기 ---------- */
        itemPreferences = getSharedPreferences(SharedPrefFile, MODE_PRIVATE);
        if(userUid == null || refrigeratorId == null || category == null || type == null) {
            userUid = itemPreferences.getString("userUid", null);
            refrigeratorId = itemPreferences.getString("ID", null);
            category = itemPreferences.getString("category", null);
            type = itemPreferences.getString("type", null);
        }
        selectedCategoryTextView = findViewById(R.id.selectedCategoryTextView);
        selectedCategoryTextView.setText(type + " " + category);

        /* ---------- 초기화 ---------- */
        allItems = new ArrayList<>();

        /* ---------- View Model ---------- */
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        viewModel.setUserUid(userUid);
        viewModel.setRefrigeratorId(refrigeratorId);
        viewModel.setCategory(category);
        viewModel.setType(type);

        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        //checkPermissions(permissions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ItemActivity", "onStart()");
        changeFragment(currentFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = itemPreferences.edit();

        preferencesEditor.putString("userUid", userUid);
        preferencesEditor.putString("ID", refrigeratorId);
        preferencesEditor.putString("category", category);
        preferencesEditor.putString("type", type);

        preferencesEditor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ((Activity)v.getContext()).getMenuInflater().inflate(R.menu.item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    public String getUserUid() { return userUid; }
    public String getRefrigeratorId() { return refrigeratorId; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public ItemViewModel getViewModel() { return viewModel; }
    public ArrayList<Item> getAllItems() { return allItems; }
    public String getClickItemName() { return clickItemName; }
    public void setClickItemName(String clickItemName) { this.clickItemName = clickItemName; }
    public Uri getUri() { return this.uri; }
    public Bitmap getBitmap() { return this.bitmap; }
    public InputImage getInputImage() { return this.inputImage; }

    private void checkPermissions(String[] permissions) {
        ArrayList<String> targetList = new ArrayList<>();

        for(int i=0; i<permissions.length; i++) {
            String curPermission = permissions[i];
            int permissionCheck = ContextCompat.checkSelfPermission(this, curPermission);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {

            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, curPermission)) {

                } else {
                    targetList.add(curPermission);
                }
            }
        }

        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);

        ActivityCompat.requestPermissions(this, targets, 101);
    }

    // 인자로 받은 fragment로 이동하는 함수
    public void changeFragment(int fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1: // ItemFragment 호출
                ItemFragment  itemFrag = new ItemFragment();
                transaction.replace(R.id.fragmentContainerView, itemFrag);
                transaction.commit();
                break;

            case 2: // ItemInformationFragment 호출, 클릭한 item 이름 전달해야함
                ItemInformationFragment itemInfoFrag = new ItemInformationFragment(clickItemName);
                transaction.replace(R.id.fragmentContainerView, itemInfoFrag);
                transaction.commit();
                break;

            case 3: // AddItemFragment 호출
                AddItemFragment addItemFrag = new AddItemFragment();
                transaction.replace(R.id.fragmentContainerView, addItemFrag);
                transaction.commit();
                break;

            case 4: // CalendarFragment 호출
                CalendarFragment calendarFrag = new CalendarFragment();
                transaction.replace(R.id.fragmentContainerView, calendarFrag);
                transaction.commit();
                break;
        }
    }

    // 아이템 정보 업데이트하는 함수
    public void updateItemInformation() {
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText expirationDateEditText = findViewById(R.id.expirationDateEditText);
        EditText memoEditText = findViewById(R.id.memoEditText);

        String name = nameEditText.getText().toString();
        String expirationDate = expirationDateEditText.getText().toString();
        String memo = memoEditText.getText().toString();

        Item item = new Item(name, expirationDate, memo);
        int index = viewModel.getItemIndex(clickItemName);
        viewModel.updateItem(index, item);
    }

    private String getDateString(String value) {
        String str = value.replaceAll("[^0-9]", "");
        int index = 0;
        if((index = str.indexOf('2')) != -1) {
            str = str.substring(index, str.length());
        }

        // 유통기한 데이터는 2로 시작해야 함
        if(!str.startsWith("2")) {
            return "null";
        }

        // 2020년 유통기한에 대한 처리
        if(str.startsWith("20") && Integer.parseInt(str.substring(2, 4)) < 13) {
            str = "20" + str;
        }

        // 여섯 자리 유통기한 포맷의 경우 여덟 자리 포맷으로 수정
        str = !str.startsWith("20") ? "20" + str : str;

        // 여덟자리인지 확인
        if(str.substring(0, 8).length() != 8) {
            return "null";
        }

        String year = str.substring(0, 4);
        String month = str.substring(4, 6);
        String date = str.substring(6, 8);

        str = year + "-" + month + "-" + date;


        return str;
    }

    public Date StringToDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try{
            d = format.parse(date);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return d;
    }


    /*
     * ---------- ItemFragment layout method ---------- *
     */
    // + 버튼 클릭 -> AddItemFragment 이동
    public void addItemFloatingButtonClick(View v) {
        changeFragment(addItemFragment);
    }

    // 아이템 클릭 -> ItemInformationFragment 이동, 클릭한 아이템 알려줘야함
    public void itemClick(View v) {
        TextView textView = (TextView) v;
        Log.e("ItemAcitivity", (String) textView.getText() + " Click");

        clickItemName = textView.getText().toString();
        changeFragment(itemInformationFragment);
    }

    // <- 버튼 클릭 -> CategoryActivity 이동
    public void backImageClick(View v) {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
    }



    /*
     * ---------- ItemInformationFragment layout method ---------- *
     */
    // "완료" 버튼 클릭 -> 수정된 정보 업데이트하고, itemFragment로 이동
    public void finishButtonClick(View v) {
        updateItemInformation();
        changeFragment(itemFragment);
    }


    /*
     * ---------- CalendarFragment layout method ---------- *
     */
    public void calendarImageClick(View v) {
        changeFragment(calendarFragment);
    }
}