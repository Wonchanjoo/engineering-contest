package com.example.refrigerator_management_app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;

public class RefrigeratorViewModel extends ViewModel {
    public MutableLiveData<ArrayList<String>> refrigeratorsLivedata = new MutableLiveData<>();
    public ArrayList<String> refrigerators = new ArrayList<>();

    private int refrigeratorPos; // 냉장고 위치

    private String userUid, refrigeratorName;

    // 냉장고 추가
    public void addRefrigerator(String name) {
        refrigerators.add(name); // 냉장고 리스트에 냉장고 추가
        Collections.sort(refrigerators); // 오름차순으로 정렬
        refrigeratorsLivedata.setValue(refrigerators);

        Log.e("RefrigeratorViewModel", name + "냉장고 추가");
    }

    // 냉장고 삭제
    public void deleteRefrigerator(int pos) {
        String name = refrigerators.get(pos); // 클릭한 냉장고의 이름
        refrigerators.remove(pos); // 냉장고 리스트에서 클릭한 냉장고 삭제
        Collections.sort(refrigerators); // 오름차순으로 정렬
        refrigeratorsLivedata.setValue(refrigerators);

        Log.e("RefrigeratorViewModel", name + "냉장고 삭제");
    }

    /* ---------- getter setter ---------- */
    public int getRefrigeratorsSize() { return refrigerators.size(); }

    public String getUserUid() { return userUid; }
    public void setUserUid(String uid) { this.userUid = uid; }

    public int getRefrigeratorPos() { return refrigeratorPos; }
    public void setRefrigeratorPos(int refrigeratorPos) { this.refrigeratorPos = refrigeratorPos; }

    public String getRefrigeratorName(int pos) {
        refrigeratorName = refrigerators.get(pos);
        return refrigeratorName;
    }
}
