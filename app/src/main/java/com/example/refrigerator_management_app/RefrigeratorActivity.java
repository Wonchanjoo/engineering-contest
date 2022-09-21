package com.example.refrigerator_management_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.refrigerator_management_app.databinding.ActivityRefrigeratorBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RefrigeratorActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private String userUid;

    private ActivityRefrigeratorBinding activityRefrigeratorBinding;
    private InformationDialog informationDialog; // 안내 다이얼로그
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private RecyclerView refrigeratorRecyclerView;
    private RefrigeratorAdapter refrigeratorAdapter;
    private RefrigeratorViewModel refrigeratorViewModel;
    private AddRefrigeratorDialog addRefrigeratorDialog;
    private ImageView addButton, logoutButton; // 냉장고 추가, 로그아웃 버튼;

    private SharedPreferences refrigeratorPreferences;
    private String SharedPrefFile = "com.example.android.MyApplication3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRefrigeratorBinding = ActivityRefrigeratorBinding.inflate(getLayoutInflater());
        setContentView(activityRefrigeratorBinding.getRoot());

        /* ---------- Intent 받아와서 uid 초기화 ---------- */
        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid");

        /* ---------- uid 값을 저장하고 있기 위해 ---------- */
//        refrigeratorPreferences = getSharedPreferences(SharedPrefFile, MODE_PRIVATE);
//        if(userUid == null)
//            userUid = refrigeratorPreferences.getString("Uid", null);

        /* ---------- 초기화 ---------- */
        refrigeratorViewModel = new ViewModelProvider(this).get(RefrigeratorViewModel.class);
        refrigeratorRecyclerView = activityRefrigeratorBinding.mainRecyclerView;
        refrigeratorAdapter = new RefrigeratorAdapter(RefrigeratorActivity.this, refrigeratorViewModel, userUid);
        addButton = activityRefrigeratorBinding.mainAddButton;
        logoutButton = activityRefrigeratorBinding.mainLogOutButton;


        /* ---------- View Model ---------- */
        refrigeratorViewModel = new ViewModelProvider(this).get(RefrigeratorViewModel.class);
        refrigeratorViewModel.setUserUid(userUid); // refrigeratorViewModel에게 userUid 넘겨주기

        /* ---------- Recycler View ---------- */
        refrigeratorRecyclerView.setAdapter(refrigeratorAdapter); // refrigeratorRecyclerView와 refrigeratorAdapter 연결
        refrigeratorRecyclerView.setLayoutManager(new GridLayoutManager(this,2)); // 한 줄에 최대 2개의 냉장고를 배치한다.
        // refrigeratorRecyclerView의 크기를 고정시킨다.
        // 아이템이 삽입/삭제될 때마다 전체 레이아웃의 크기를 다시 설정하지 않고 아이템들의 위치만 설정하여 효율적이다.
        refrigeratorRecyclerView.setHasFixedSize(true);
        refrigeratorRecyclerView.addItemDecoration(new RefrigeratorRecyclerviewDeco(40)); // 냉장고들 사이의 간격을 정한다.

        final Observer<ArrayList<String>> userObserver = strings -> {
            refrigeratorAdapter.notifyDataSetChanged();
        };
        refrigeratorViewModel.refrigeratorsLivedata.observe(this, userObserver);
        registerForContextMenu(refrigeratorRecyclerView); // 컨텍스트 메뉴 생성을 위해 등록

        /* ---------- 버튼 Click Listener ---------- */
        // addButton 클릭
        addButton.setOnClickListener(view -> {
            // -1 : 새로운 냉장고 추가
            addRefrigeratorDialog = new AddRefrigeratorDialog(RefrigeratorActivity.this, refrigeratorViewModel, -1, userUid);
            addRefrigeratorDialog.show();
        });

        // logoutButton 클릭
        logoutButton.setOnClickListener(view -> {
            informationDialog = new InformationDialog(RefrigeratorActivity.this, 2);
            informationDialog.show();
        });

        showRefrigeratorList(); // 데이터베이스에서 가져와 뷰모델에 삽입하여 사용자 화면에 냉장고를 보이게 함.
        checkPermission(Manifest.permission.POST_NOTIFICATIONS, NOTIFICATION_PERMISSION_CODE);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 클릭한 위치 수정
            case R.id.edit:
                int position = refrigeratorViewModel.getRefrigeratorPos(); // 수정할 냉장고의 위치
                addRefrigeratorDialog = new AddRefrigeratorDialog(RefrigeratorActivity.this, refrigeratorViewModel, position, userUid);
                addRefrigeratorDialog.show();
                break;

            // 클릭한 위치 삭제
            case R.id.deleteCategory:
                String itemName = refrigeratorViewModel.getRefrigeratorName(refrigeratorViewModel.getRefrigeratorPos()); // 삭제할 냉장고의 이름
                // 파이어베이스에서 냉장고 삭제
                databaseReference.child("UserAccount").child(userUid).child("냉장고").child(itemName).removeValue();
                // 뷰모델에서 냉장고 삭제
                // onChildRemoved를 쓸까 하였는데, 데이터베이스에서 지워진 냉장고의 이름을 뷰모델에서 찾아서 위치를 알아낸 후
                // 삭제하는 과정이 비효율적인 것 같아 뷰모델의 deleteRefrigerator 사용함.
                refrigeratorViewModel.deleteRefrigerator(refrigeratorViewModel.getRefrigeratorPos());
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        informationDialog = new InformationDialog(RefrigeratorActivity.this, 3);
        informationDialog.show();
    }

    // 사용자 화면에 냉장고를 보여주는 메소드
    private void showRefrigeratorList(){
        databaseReference.child("UserAccount").child(userUid).child("냉장고").addChildEventListener(new ChildEventListener() {
            @Override
            // 데이터베이스에 추가되면 자동으로 뷰모델에도 추가되도록 함.
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 데이터베이스에서 가져와서 뷰모델에 냉장고 추가
                refrigeratorViewModel.addRefrigerator(snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(RefrigeratorActivity.this, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(RefrigeratorActivity.this, new String[] {permission}, requestCode);
        }else{
            Toast.makeText(RefrigeratorActivity.this,"Permission already Granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==NOTIFICATION_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(RefrigeratorActivity.this,"Notification Permission Granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(RefrigeratorActivity.this,"Notification Permission Denied",Toast.LENGTH_SHORT).show();
            }

        }
    }
}