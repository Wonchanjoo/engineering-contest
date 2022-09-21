package com.example.refrigerator_management_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FreezeCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreezeCategoryFragment extends Fragment {
    private String userUid;
    private String refrigeratorId;

    private CategoryActivity categoryActivity;
    private AddCategoryDialog addCategoryDialog;
    private Button addFreezeCategoryBtn;

    private CategoryViewModel viewModel;
    private RecyclerView recyclerView;
    private FreezeCategoryAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FreezeCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreezeCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreezeCategoryFragment newInstance(String param1, String param2) {
        FreezeCategoryFragment fragment = new FreezeCategoryFragment();
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
        /* ---------- CategoryActivity에서 uid, id 가져오기 ---------- */
        userUid = ((CategoryActivity)getActivity()).getUserUid();
        refrigeratorId = ((CategoryActivity)getActivity()).getRefrigeratorId();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freeze_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ---------- 초기화 ---------- */
        categoryActivity = (CategoryActivity)getActivity();
        addFreezeCategoryBtn = getView().findViewById(R.id.addFreezeCategoryBtn);
        viewModel = categoryActivity.getViewModel();
        recyclerView = (RecyclerView) getView().findViewById(R.id.freezeCategoryRecyclerView);
        adapter = new FreezeCategoryAdapter(viewModel, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        /* ---------- 리스너 ---------- */
        addFreezeCategoryBtn.setOnClickListener(this::addFreezeCategoryBtnClick);

        registerForContextMenu(recyclerView);

        final Observer<ArrayList<String>> freezeObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                adapter.notifyDataSetChanged(); // 어댑터에게 데이터가 변경되었다는 것을 알림
            }
        };
        viewModel.freezeCategorysLiveData.observe(getActivity(), freezeObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        showFreezeCategoryList();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.category_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteCategory:
                // 카테고리 삭제
                viewModel.deleteFreezeCategory(viewModel.longClickPosition);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void showFreezeCategoryList() {
        Log.e("FreezeCategoryFragment", "viewModel size = " + viewModel.getFreezeCategorySize());
        FirebaseDatabase.getInstance().getReference().child("UserAccount").child(userUid).child("냉장고").child(refrigeratorId).child("냉동").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // 해당 냉장고의 "냉장" 아래에 새로운 아이템이 추가된 경우 뷰모델에 추가
                String category = snapshot.getKey();
                viewModel.addFreezeCategory(category);
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

    // 냉동 카테고리 추가하는 버튼을 누르면 실행되는 함수
    public void addFreezeCategoryBtnClick(View v) {
        addCategoryDialog = new AddCategoryDialog(getContext(), viewModel, "냉동");
        addCategoryDialog.show();
    }
}