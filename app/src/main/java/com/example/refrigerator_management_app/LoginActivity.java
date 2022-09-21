package com.example.refrigerator_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.refrigerator_management_app.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;
    private FirebaseAuth firebaseAuth; // 파이어베이스 인증
    private DatabaseReference databaseReference; // 실시간 데이터베이스
    private WarningDialog warningDialog; // 경고 다이얼로그
    private EditText firstLoginEmail, firstLoginPw; // 로그인 입력 필드 (아이디, 비밀번호)
    private ImageView loginButton, gotoJoinButton; // 로그인, 회원가입 하러가기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        // 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firstLoginEmail = activityLoginBinding.loginEmailText;
        firstLoginPw = activityLoginBinding.loginPwText;
        loginButton = activityLoginBinding.loginButton;
        gotoJoinButton = activityLoginBinding.loginGoToJoinButton;

        // 로그인 버튼 클릭
        loginButton.setOnClickListener(view -> {
            // 사용자가 입력한 email과 password
            String email = firstLoginEmail.getText().toString();
            String password = firstLoginPw.getText().toString();
            Log.e("LoginActivity", "email = " + email + ", password = " + password);

            if (email.equals("") || password.equals("")){
                warningDialog = new WarningDialog(LoginActivity.this, 1);
                warningDialog.show();
            }
            else {
                // 로그인 요청
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 로그인 성공
                            // 현재 로그인되어 있는 user 가져옴.
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            // 사용자의 uid를 가져온다.
                            String userUid = firebaseUser.getUid();
                            // 사용자의 uid를 이용하여 사용자의 id를 가져온다.
                            databaseReference.child("UserAccount").child(userUid).child("userId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    // 실패시
                                    if (!task.isSuccessful()) {
                                    }
                                    // 성공시
                                    else {
                                        // 사용자의 uid를 다시 가져온다.
                                        // 왜인지 모르겠으나 오류 때문에 위에서 사용한 userUid 다시 사용 못 함.
                                        String userUid = firebaseUser.getUid();
                                        // userId를 가져온다.
                                        String userId = (String) task.getResult().getValue();
                                        // 인텐트 객체에 userUID와 userId를 넣어서 RefrigeratorActivity로 보낸다.
                                        Intent intent = new Intent(LoginActivity.this, RefrigeratorActivity.class);
                                        intent.putExtra("userUid", userUid);
                                        intent.putExtra("userID", userId);
                                        Log.e("Login userUid : ",userUid);
                                        Log.e("Login userId : ",userId);
                                        Log.e("로그인 성공", "Id : " + userId +" email : " + email + " password :  " + password);
                                        // MainActivity(냉장고 추가 화면)으로 넘어간다.
                                        startActivity(intent);
                                        // LoginActivity 종료
                                        finish();
                                    }
                                }
                            });

                        }
                        else {
                            // 로그인 실패
                            warningDialog = new WarningDialog(LoginActivity.this, 7);
                            warningDialog.show();
                            Log.e("로그인 실패", "");
                        }
                    }
                });
            }
        });

        // 회원가입 버튼 클릭
        gotoJoinButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
            startActivity(intent);
        });
    }
}