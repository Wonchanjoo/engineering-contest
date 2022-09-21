package com.example.refrigerator_management_app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ItemViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Item>> itemsLiveData = new MutableLiveData<>();
    public ArrayList<Item> items = new ArrayList<>();

    public int longClickPosition;

    private DatabaseReference databaseItemReference;
    private String userUid;
    private String refrigeratorId;
    private String category;
    private String type;

    public ItemViewModel() {
        databaseItemReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addItem(Item item) {
        if(items.contains(item))
            return;

        items.add(item);
        createItemInDatabase(item);
        Collections.sort(items, new ItemNameComparator());

        if(items.size() == 1)
            deleteTempCategoryInDatabase();

        itemsLiveData.setValue(items);
    }

    public void deleteItem(int position) {
        Log.e("ItemViewModel", "deleteItem(), position = " + position);
        Item temp = items.get(position);
        items.remove(position); // 뷰모델에서 삭제
        deleteItemInDatabase(temp); // 데이터베이스에서 삭제
        itemsLiveData.setValue(items);
    }

    public void deleteItem(Item item) {
        int position = getItemIndex(item.getName());
        deleteItem(position);
    }

    public void updateItem(int index, Item item) {
        updateItemInDatabase(items.get(index), item);
        items.set(index, item);
        Collections.sort(items, new ItemNameComparator());
        itemsLiveData.setValue(items);
    }

    public int getItemIndex(String itemName) {
        for(int i=0; i<items.size(); i++) {
            Item temp = items.get(i);
            if(itemName.equals(temp.getName()))
                return i;
        }
        return -1;
    }

    public void initItems() {
        items.clear();
        itemsLiveData.setValue(items);
    }

    /* ---------- get set ---------- */
    public Item getItem(int index) { return items.get(index); }
    public void setItem(int index, Item item) { items.set(index, item); }
    //public int getItemIndex(Item item) { return items.indexOf(item); } // item의 index를 반환
    public String getUserUid() { return userUid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }
    public String getRefrigeratorId() { return refrigeratorId; }
    public void setRefrigeratorId(String refrigeratorId) { this.refrigeratorId = refrigeratorId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getItemSize() { return items.size(); }

    /* ---------- 데이터베이스 ---------- */
    public void createItemInDatabase(Item item) {
        Log.e("ItemViewModel", item.getName() + " 데이터 베이스에 추가");
        databaseItemReference = FirebaseDatabase.getInstance().getReference();
        databaseItemReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category).child(item.getName()).child("이름").setValue(item.getName());
        databaseItemReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category).child(item.getName()).child("유통기한").setValue(item.getExpirationDate());
        databaseItemReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category).child(item.getName()).child("메모").setValue(item.getMemo());
    }

    public void deleteItemInDatabase(Item item) {
        databaseItemReference = FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category);
        databaseItemReference.child(item.getName()).removeValue();
    }

    public void updateItemInDatabase(Item item1, Item item2) { // item1을 item2로 수정
        // item1을 삭제하고 item2를 추가
        deleteItemInDatabase(item1);
        createItemInDatabase(item2);
    }

    public void deleteTempCategoryInDatabase() {
        databaseItemReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category).child("temp").removeValue();
    }


    class ItemNameComparator implements Comparator<Item> {
        @Override
        public int compare(Item item1, Item item2) {
            int i = item1.getName().compareTo(item2.getName());
            if(i > 0)
                return 1;
            else if(i < 0)
                return -1;
            else
                return 0;
        }
    }
}
