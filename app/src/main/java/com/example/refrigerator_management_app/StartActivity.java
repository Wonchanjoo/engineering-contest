package com.example.refrigerator_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.refrigerator_management_app.databinding.ActivityStartBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ActivityStartBinding activityStartBinding; // ActivityStartBinding binding
    private FirebaseAuth firebaseAuth; // 파이어베이스 인증
    private DatabaseReference databaseReference; // 실시간 데이터베이스
    private FirebaseAuth.AuthStateListener authStateListener;
    // 로그인 액티비티 이동 버튼, 회원가입 액티비티 이동 버튼, 환경설정 액티비티 이동 버튼
    private ImageView startLoginButton, startJoinButton, startSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityStartBinding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(activityStartBinding.getRoot());

        // 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        startLoginButton = activityStartBinding.startLoginButton;
        startJoinButton = activityStartBinding.startJoinButton;
        startSettingButton = activityStartBinding.startSettingButton;

        /*
         * 사용자의 현재 로그인 상태 여부를 알게 해준다.
         * 만약 사용자가 앱을 켰을 때 사용자가 로그인 상태이면
         * StartActivity에서 바로 RefrigeratorActivity로 이동하게 된다.
         */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                // 사용자가 로그인 중이라면
                if (firebaseUser != null) {
                    // 사용자의 uid를 가져온다.
                    String userUid = firebaseUser.getUid();
                    // 사용자의 uid를 이용하여 사용자의 id를 가져온다.
                    databaseReference.child("UserAccount").child(userUid).child("userId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            // 실패시
                            if (!task.isSuccessful()) { }
                            // 성공시
                            else {
                                // 사용자의 uid를 다시 가져온다.
                                // 왜인지 모르겠으나 오류 때문에 위에서 사용한 userUid 다시 사용 못 함.
                                String userUid = firebaseUser.getUid();
                                // userId를 가져온다.
                                String userId = (String) task.getResult().getValue();
                                // 인텐트 객체에 userUID와 userId를 넣어서 RefrigeratorActivity로 보낸다.
                                Intent intent = new Intent(StartActivity.this, RefrigeratorActivity.class);
                                intent.putExtra("userUid", userUid);
                                intent.putExtra("userID", userId);
//                                Log.e("Login userUid : ",userUid);
//                                Log.e("Login userId : ",userId);
                                startActivity(intent);
                                // StartActivity 종료
                                finish();
                            }
                        }
                    });


                } else {
                    // StartActivity 화면이 보여진다.
                }
            }
        };

        // 로그인 액티비티 이동 버튼 클릭
        startLoginButton.setOnClickListener(view -> {
            // 로그인 화면으로 이동
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // 회원가입 액티비티 이동 버튼 클릭
        startJoinButton.setOnClickListener(view -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(StartActivity.this, JoinActivity.class);
            startActivity(intent);
        });

        // 환경설정 액티비티 이동 버튼 클릭
        startSettingButton.setOnClickListener(view -> {
            // 환경설정 화면으로 이동
            Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}