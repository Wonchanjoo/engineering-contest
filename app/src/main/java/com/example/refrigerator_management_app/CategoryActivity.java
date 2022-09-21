package com.example.refrigerator_management_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity {
    private final int refrigerationCategoryFragment = 1;
    private final int freezeCategoryFragment = 2;

    private ImageView refrigerationImage;
    private ImageView freezeImage;
    private ImageView settingIcon;
    private ImageView toRefrigeratorActivityBtn;
    private TextView refrigeratorIdTextView;

    private String userUid; // RefrigeratorActivity에서 전달된 냉장고 Uid
    private String refrigeratorId; // RefrigeratorActivity에서 전달된 냉장고 id
    private String type;

    private CategoryViewModel viewModel;

    private SharedPreferences categoryPreferences;
    private String SharedPrefFile = "com.example.android.MyApplication3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /* ---------- 초기화 ---------- */
        refrigerationImage = findViewById(R.id.refrigerationImage);
        freezeImage = findViewById(R.id.freezeImage);
        settingIcon = findViewById(R.id.settingIcon);
        toRefrigeratorActivityBtn = findViewById(R.id.toRefrigeratorActivity);
        type = "냉장"; // 냉장 프래그먼트로 시작하므로 "냉장"으로 초기화
        refrigeratorIdTextView = findViewById(R.id.refrigeratorIdTextView);

        /* ---------- 리스너 ---------- */
        refrigerationImage.setOnClickListener(this::refrigerationImageClick);
        freezeImage.setOnClickListener(this::freezeImageClick);
        settingIcon.setOnClickListener(this::settingIconClick);
        toRefrigeratorActivityBtn.setOnClickListener(this::toRefrigeratorActivity);

        /* ---------- Intent 받아와서 uid, id 초기화 ---------- */
        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid"); // 냉장고 Uid
        refrigeratorId = intent.getStringExtra("refrigeratorID"); // 냉장고 id

        /* ---------- uid, id가 null이면 저장소에서 값 가져오기 ---------- */
        categoryPreferences = getSharedPreferences(SharedPrefFile, MODE_PRIVATE);
        if(userUid == null || refrigeratorId == null) {
            userUid = categoryPreferences.getString("userUid", null);
            refrigeratorId = categoryPreferences.getString("ID", null);
        }
        refrigeratorIdTextView.setText(refrigeratorId);

        /* ---------- View Model ---------- */
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.setUserUid(userUid);
        viewModel.setRefrigeratorId(refrigeratorId);

        // 리싸이클러뷰, 어댑터는 각 프래그먼트에서 생성
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = categoryPreferences.edit();

        preferencesEditor.putString("userUid", userUid);
        preferencesEditor.putString("ID", refrigeratorId);

        preferencesEditor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ((Activity)view.getContext()).getMenuInflater().inflate(R.menu.category_context_menu, contextMenu);
        Log.e("MainActivity", "onCreateContextMenu()");
    }

    public String getUserUid() { return userUid; }
    public String getRefrigeratorId() { return refrigeratorId; }
    public CategoryViewModel getViewModel() { return viewModel; }

    // 인자로 받은 fragment로 이동하는 함수
    public void changeFragment(int fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1: // RefrigerationCategoryFragment 호출
                RefrigerationCategoryFragment refrigerationCategoryFrag = new RefrigerationCategoryFragment();
                transaction.replace(R.id.categoryFragmentContainerView, refrigerationCategoryFrag);
                transaction.commit();
                break;

            case 2: // FreezeCategoryFragment 호출
                FreezeCategoryFragment freezeCategoryFrag = new FreezeCategoryFragment();
                transaction.replace(R.id.categoryFragmentContainerView, freezeCategoryFrag);
                transaction.commit();
                break;
        }
    }

    // "냉장" 클릭 -> refrigerationCategoryFragment로 전환, image 바꾸기
    public void refrigerationImageClick(View v) {
        Log.e("CategoryActivity", "냉장 버튼 Click");
        type = "냉장";

        refrigerationImage.setImageResource(R.drawable.selected_refrigeration_btn);
        freezeImage.setImageResource(R.drawable.unselected_freeze_btn);

        changeFragment(refrigerationCategoryFragment);
    }

    // "냉동" 클릭 -> freezeCategoryFragment로 전환, image 바꾸기
    public void freezeImageClick(View v) {
        Log.e("CategoryActivity", "냉동 버튼 Click");
        type = "냉동";

        refrigerationImage.setImageResource(R.drawable.unselected_refrigeration_btn);
        freezeImage.setImageResource(R.drawable.selected_freeze_btn);

        changeFragment(freezeCategoryFragment);
    }

    // "설정" 아이콘 클릭 -> SettingsActivity 이동
    public void settingIconClick(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // <- 아이콘 클릭 -> RefrigeratorActivity 이동
    public void toRefrigeratorActivity(View v) {
        Intent intent = new Intent(this, RefrigeratorActivity.class);
        intent.putExtra("userUid", userUid);
        startActivity(intent);
    }
}