package com.example.refrigerator_management_app;

import android.graphics.Bitmap;

import java.util.Objects;

public class Item {
    private String name; // 이름
    private String expirationDate; // 유통기한
    private String memo; // 메모
    private int remainDays; // 남은 기간
    private Bitmap bitmap;

    public Item() {
        name = "초기화 x";
        expirationDate = "초기화 x";
        memo = "초기화 x";
    }

    public Item(String name, String expirationDate, String memo) {
        this.name = name;
        this.expirationDate = expirationDate;
        this.memo = memo;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public int getRemainDays() { return remainDays; }
    public void setRemainDays(int remainDays) { this.remainDays = remainDays; }

    @Override
    public boolean equals(Object o) {
        boolean same = false;
        if(o != null && o instanceof Item) {
            same = this.name.equals((((Item) o).getName()));
        }
        return same;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expirationDate, memo);
    }
}
