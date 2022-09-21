package com.example.refrigerator_management_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {
    private String userUid;
    private String refrigeratorId;
    private String category;
    private String type;

    private ItemViewModel viewModel;
    private RecyclerView itemRecyclerView;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> allItems;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* ---------- ItemActivity에서 uid, id, category 가져오기 ---------- */
        userUid = ((ItemActivity)getActivity()).getUserUid();
        refrigeratorId = ((ItemActivity)getActivity()).getRefrigeratorId();
        category = ((ItemActivity)getActivity()).getCategory();
        type = ((ItemActivity)getActivity()).getType();

        Log.e("ItemFragment", "category = " + category);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allItems = ((ItemActivity)getActivity()).getAllItems();

        /* ---------- View Model ---------- */
        viewModel = ((ItemActivity) getActivity()).getViewModel();

        /* ---------- Recycler View ---------- */
        itemRecyclerView = (RecyclerView) getView().findViewById(R.id.itemRecyclerView);
        itemAdapter = new ItemAdapter(viewModel);
        itemRecyclerView.setAdapter(itemAdapter);
        itemRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        itemRecyclerView.setHasFixedSize(true);

        registerForContextMenu(itemRecyclerView);

        final Observer<ArrayList<Item>> observer = new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(ArrayList<Item> items) {
                itemAdapter.notifyDataSetChanged(); // 어댑터에게 데이터가 변경되었다는 것을 알리기 위해
            }
        };
        viewModel.itemsLiveData.observe(getActivity(), observer);
    }

    @Override
    public void onStart() {
        super.onStart();
        showItemList();

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        // super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteItem:
                // 아이템 삭제
                viewModel.deleteItem(viewModel.longClickPosition);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void showItemList() {
        Log.e("ItemFragment", "viewModel size = " + viewModel.getItemSize());
        FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child(type).child(category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 해당 카테고리 아래에 새로운 아이템이 추가된 경우 뷰모델에 추가
                String value = snapshot.getValue(Object.class).toString();
                if(value == null || value.startsWith("{-"))
                    return;

                StringTokenizer st = new StringTokenizer(value, ",");

                if(st.countTokens() > 1 && !value.equals("temp")) {
                    String memo = st.nextToken().substring(4);
                    String name = st.nextToken().substring(4);
                    String expirationDate = st.nextToken().substring(6);
                    expirationDate = expirationDate.substring(0, expirationDate.length() - 1); // 맨 뒤 괄호 제거

                    // 유통기한이 2로 시작하면
                    if (expirationDate.startsWith("2")) {
                        Item item = new Item(name, expirationDate, memo);

                        // 유통기한이 지난 경우 데이터베이스에서 아이템 삭제
                        if(isExpiredItem(item.getExpirationDate())) {
                            viewModel.deleteItemInDatabase(item);
                            Log.e("ItemActivity", item.getName() + "의 유통기한이 지남, " + item.getExpirationDate());
                        }
                        // 유통기한이 지나지 않았으면 viewModel에 넣어주기
                        else {
                            viewModel.addItem(item);
                            if(!allItems.contains(item))
                                allItems.add(item);
                        }
                    }
                }
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

    // 호출된 시점 기준으로 유통기한이 지났으면 true, 아니면 false
    public boolean isExpiredItem(String expirationDate) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dataFormat.format(date);

        // 현재 년월일
        String nowYearStr = time.substring(0, 4);
        String nowMonthStr = time.substring(5, 7);
        String nowDayStr = time.substring(8, 10);
        int nowYear = Integer.parseInt(nowYearStr);
        int nowMonth = Integer.parseInt(nowMonthStr);
        int nowDay = Integer.parseInt(nowDayStr);

        // 유통기한 년월일
        String yearStr = expirationDate.substring(0, 4);
        String monthStr = expirationDate.substring(5, 7);
        String dayStr = expirationDate.substring(8, 10);
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        int temp = year - nowYear; // 년도의 차
        if(temp < 0) // 년도의 차가 음수면 유통기한이 이미 지난 것
            return true;
        month += temp * 12;

        temp = month - nowMonth;
        if(temp < 0) // 월 차가 음수면 유통기한이 이미 지난 것
            return true;
        day += temp * 12;

        temp = day - nowDay;
        if(temp < 0) // 일 차가 음수면 유통기한이 이미 지난 것
            return true;

        return false;
    }
}