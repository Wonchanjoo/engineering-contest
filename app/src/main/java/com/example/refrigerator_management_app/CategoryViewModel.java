package com.example.refrigerator_management_app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class CategoryViewModel extends ViewModel {
    public MutableLiveData<ArrayList<String>> refrigerationCategorysLiveData = new MutableLiveData<>(); // "냉장" 카테고리 live data
    public ArrayList<String> refrigerationCategorys = new ArrayList<>(); // "냉장" 카테고리 array list

    public MutableLiveData<ArrayList<String>> freezeCategorysLiveData = new MutableLiveData<>(); // "냉동" 카테고리 live data
    public ArrayList<String> freezeCategorys = new ArrayList<>(); // "냉동" 카테고리 array list

    public int longClickPosition;

    private DatabaseReference databaseCategoryReference;
    private String userUid;
    private String refrigeratorId;

    public CategoryViewModel() {
        databaseCategoryReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addRefrigerationCategory(String category) {
        if(refrigerationCategorys.contains(category)) // 이미 같은 카테고리를 가지고 있으면 X
            return;

        Log.e("CategoryViewModel", "카테고리 " + category + " 추가");
        refrigerationCategorys.add(category);
        createRefrigerationCategoryInDatabase(category); // 데이터베이스에도 추가
        Collections.sort(refrigerationCategorys); // 정렬
        refrigerationCategorysLiveData.setValue(refrigerationCategorys); // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    public void addFreezeCategory(String category) {
        if(freezeCategorys.contains(category)) // 이미 같은 카테고리를 가지고 있으면 X
            return;

        freezeCategorys.add(category);
        createFreezeCategoryInDatabase(category); // 데이터베이스에도 추가
        Collections.sort(freezeCategorys); // 정렬
        freezeCategorysLiveData.setValue(freezeCategorys); // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    public void deleteRefrigerationCategory(int position) {
        String temp = refrigerationCategorys.get(position);
        refrigerationCategorys.remove(position); // 뷰모델에서 삭제
        deleteRefrigerationCategoryInDatabase(temp); // 데이터베이스에서 삭제
        refrigerationCategorysLiveData.setValue(refrigerationCategorys); // 옵저버에서 라이브데이터가 변경된 것을 알리기 위해
    }

    public void deleteFreezeCategory(int position) {
        String temp = freezeCategorys.get(position);
        freezeCategorys.remove(position); // 뷰모델에서 삭제
        deleteFreezeCategoryInDatabase(temp); // 데이터베이스에서 삭제
        freezeCategorysLiveData.setValue(freezeCategorys); // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    public int getRefrigerationCategorySize() {
        return refrigerationCategorys.size();
    }
    public int getFreezeCategorySize() { return freezeCategorys.size(); }

    /* ---------- get set ---------- */
    public String getUserUid() { return userUid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }
    public String getRefrigeratorId() { return refrigeratorId; }
    public void setRefrigeratorId(String refrigeratorId) { this.refrigeratorId = refrigeratorId; }

    /* ---------- 데이터베이스 ---------- */
    // "냉장" 아래 카테고리 추가
    public void createRefrigerationCategoryInDatabase(String category) {
        // 카테고리를 처음 만들었을 때 카테고리 안에 아무것도 들어있지 않기 때문에 데이터베이스에 제대로 카테고리가 만들어지지 않음
        // 그래서 카테고리 아래에 temp라는 임시 폴더를 만들어두었고, 액티비티에는 안 보이도록 함 (데이터베이스에서 보면 카테고리 아래에 temp가 존재)
        databaseCategoryReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child("냉장").child(category).child("temp").push().setValue("");
    }
    // "냉동" 아래 카테고리 추가
    public void createFreezeCategoryInDatabase(String category) {
        databaseCategoryReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child("냉동").child(category).child("temp").push().setValue("");
    }

    public void deleteRefrigerationCategoryInDatabase(String category) {
        databaseCategoryReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child("냉장").child(category).removeValue();
    }

    public void deleteFreezeCategoryInDatabase(String category) {
        databaseCategoryReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child("냉동").child(category).removeValue();
    }
}
