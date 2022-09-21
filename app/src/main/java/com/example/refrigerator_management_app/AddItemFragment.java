package com.example.refrigerator_management_app;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {
    private final int itemFragment = 1;
    private final int itemInformationFragment = 2;
    private final int addItemFragment = 3;
    private final int calendarFragment = 4;
    private final int ITEM_IMAGE = 10;
    private final int EXPIRATION_DATE_IMAGE = 11;

    private ItemActivity itemActivity;
    private AddItemDialog addItemDialog;
    private ImageView addItemBtn;
    private ImageView returnBtn;
    private ImageView shootItemImageView;
    private ImageView shootExpirationDateBtn;

    private ItemViewModel viewModel;
    private ArrayList<Item> allItems;

    private TextRecognizer recognizer;
    private Uri uri;
    private Bitmap bitmap;
    private InputImage inputImage;
    private Bitmap itemBitmap = null;
    private InputImage itemInputImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ---------- 초기화 ---------- */
        itemActivity = (ItemActivity) getActivity();
        viewModel = ((ItemActivity)getActivity()).getViewModel();
        allItems = ((ItemActivity)getActivity()).getAllItems();
        addItemBtn = getView().findViewById(R.id.addItemBtn);
        returnBtn = getView().findViewById(R.id.returnBtn);
        shootItemImageView = getView().findViewById(R.id.shootItemImageView);
        shootExpirationDateBtn = getView().findViewById(R.id.shootExpirationDateImageView);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        /* ---------- 리스너 ---------- */
        addItemBtn.setOnClickListener(this::addItemBtnClick);
        returnBtn.setOnClickListener(this::returnBtnClick);
        shootItemImageView.setOnClickListener(this::shootItemImageViewClick);
        shootExpirationDateBtn.setOnClickListener(this::shootExpirationDataBtnClick);

        // Uri exposure 무시
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        int r = (int)(Math.random() * 5);
        switch (r) {
            case 0:
                shootItemImageView.setImageResource(R.drawable.food);
                break;
            case 1:
                shootItemImageView.setImageResource(R.drawable.bakery);
                break;
            case 2:
                shootItemImageView.setImageResource(R.drawable.fruit);
                break;
            case 3:
                shootItemImageView.setImageResource(R.drawable.vegetable);
                break;
            case 4:
                shootItemImageView.setImageResource(R.drawable.groceries);
                break;
            case 5:
                shootItemImageView.setImageResource(R.drawable.liquor);
                break;
            case 6:
                shootItemImageView.setImageResource(R.drawable.drink);
                break;
        }
    }

    // "추가하기" 버튼 클릭 -> 추가하겠냐는 다이얼로그 생성
    public void addItemBtnClick(View v) {
        itemActivity.currentFragment = itemFragment;

        EditText newNameEditText = getView().findViewById(R.id.newNameEditText);
        EditText newExpirationDateEditText = getView().findViewById(R.id.newExpirationDateEditText);
        EditText newMemoEditText = getView().findViewById(R.id.newMemoEditText);

        String newName = newNameEditText.getText().toString();
        String newExpirationDate = newExpirationDateEditText.getText().toString();
        String newMemo = newMemoEditText.getText().toString();

        addItemDialog = new AddItemDialog(getContext(), viewModel, allItems, newName, newExpirationDate, newMemo);
        addItemDialog.show();
    }

    // "유통기한 촬영" 버튼 클릭 -> 카메라로 사진 찍기
    private void shootExpirationDataBtnClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, EXPIRATION_DATE_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EXPIRATION_DATE_IMAGE && resultCode == RESULT_OK) {
            Log.e("onActivityResult", "유통기한 촬영");
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            inputImage = InputImage.fromBitmap(bitmap, 0);
            itemActivity.currentFragment = addItemFragment;
            textRecognition(recognizer); // 찍은 사진으로 유통기한 인식
        }

        if(requestCode == ITEM_IMAGE && resultCode == RESULT_OK) {
            Log.e("onActivityResult", "이미지 촬영");
            Bundle extras = data.getExtras();
            itemBitmap = (Bitmap) extras.get("data");
            itemInputImage = InputImage.fromBitmap(itemBitmap, 0);
            itemActivity.currentFragment = addItemFragment;
            setItemImage(recognizer);
        }
    }

    public void textRecognition(TextRecognizer recognizer) {
        Task<Text> result = recognizer.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String resultText = text.getText(); // 인식한 텍스트
                        resultText = getDateString(resultText);
                        Log.e("AddItemFragment", "인식한 텍스트 " + resultText);

                        EditText newExpirationDateEditText = itemActivity.findViewById(R.id.newExpirationDateEditText);
                        newExpirationDateEditText.setText(resultText);
                        shootItemImageView = itemActivity.findViewById(R.id.shootItemImageView);
                        shootItemImageView.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
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

    // item 촬영 버튼 클릭
    public void shootItemImageViewClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, ITEM_IMAGE);
        }
    }

    private void setItemImage(TextRecognizer recognizer) {
        Task<Text> result = recognizer.process(itemInputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        shootItemImageView = itemActivity.findViewById(R.id.shootItemImageView);
                        shootItemImageView.setImageBitmap(itemBitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    // "돌아가기" 버튼 클릭 -> ItemFragment로 이동
    public void returnBtnClick(View v) {
        ((ItemActivity)getActivity()).changeFragment(itemFragment);
    }
}