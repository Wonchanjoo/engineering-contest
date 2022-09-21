package com.example.refrigerator_management_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.example.refrigerator_management_app.databinding.DialogInformationBinding;
import com.google.firebase.auth.FirebaseAuth;

public class InformationDialog extends Dialog {
    private FirebaseAuth firebaseAuth; // 파이어베이스 인증
    private DialogInformationBinding dialogInformationBinding;
    private Context myContext;
    private int myMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogInformationBinding = DialogInformationBinding.inflate(getLayoutInflater());
        setContentView(dialogInformationBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        Button yesButton = dialogInformationBinding.informationDialogbuttonYes;
        Button noButton = dialogInformationBinding.informationDialogbuttonNo;
        ImageView informationText = dialogInformationBinding.informationDialogText;

        // 회원가입 성공시
        if(myMode == 1){
            informationText.setImageResource(R.drawable.goto_login);
        }
        // 로그아웃
        else if(myMode == 2){
            informationText.setImageResource(R.drawable.logout);
        }
        // 앱 종료
        else if(myMode == 3) {
            informationText.setImageResource(R.drawable.finish_application);
        }
        // mode가 설정되어있지 않는 경우
        else{
            informationText.setImageResource(R.drawable.empty_text);
            Log.e("InformationDialog의 ","drawble mode 설정이 되어있지 않음");
        }

        // 버튼 리스너 설정
        yesButton.setOnClickListener(view -> {
            Log.e("InformationDialog의 ","YES 버튼이 눌림");
            // 회원가입 성공시
            // yesButton 누르면 로그인 화면으로 돌아감.
            if(myMode == 1){
                Intent intent = new Intent(myContext, LoginActivity.class);
                myContext.startActivity(intent);
                ActivityCompat.finishAffinity((Activity) myContext);
            }
            // 로그아웃
            else if(myMode == 2){
                firebaseAuth.signOut();

                Intent intent = new Intent(myContext, StartActivity.class);
                myContext.startActivity(intent);
                ActivityCompat.finishAffinity((Activity) myContext);
                Log.e("로그아웃 성공", "");

                /*
                < 탈퇴 처리 >
                firebaseAuth.getCurrentUser().delete();
                */

            }

            // 앱 종료
            else if(myMode == 3) {
                ActivityCompat.finishAffinity((Activity) myContext);
            }

            dismiss();
        });

        noButton.setOnClickListener(view -> {
            Log.e("InformationDialog의 ","NO 버튼이 눌림");
            // 회원가입 성공시
            // noButton 누르면 처음 화면으로 돌아감.
            if(myMode == 1){
                // Intent intent = new Intent(myContext, StartActivity.class);
                // myContext.startActivity(intent);
                ActivityCompat.finishAffinity(((Activity)myContext));
            }

            dismiss();
        });

    }

    public InformationDialog(Context context, int mode) {
        super(context);
        this.myContext = context;
        this.myMode = mode;
    }
}
