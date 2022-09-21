package com.example.refrigerator_management_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.refrigerator_management_app.databinding.ActivityJoinBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinActivity extends AppCompatActivity {

    private ActivityJoinBinding activityJoinBinding; // RegisterActivity binding
    private WarningDialog warningDialog; // 경고 다이얼로그
    private InformationDialog informationDialog; // 안내 다이얼로그
    private FirebaseAuth firebaseAuth; // 파이어베이스 인증
    private DatabaseReference databaseReference; // 실시간 데이터베이스
    private ImageView joinPwCheckWarningImage; // 회원가입 비밀번호 확인 오류 이미지
    private TextView joinPwCheckWarningText; // 회원가입 비밀번호 확인 오류 텍스트
    private EditText joinID, joinEmail, joinPw, joinPwCheck; // 회원가입 입력 필드 (아이디, 이메일, 비밀번호, 비밀번호 확인)
    private ImageView joinButton; // 회원가입 버튼
    private int joinPwCheckFlag; // 회원가입 확인 절차 진행 여부
    private TextWatcher textWatcher; // 텍스트 변경 감지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityJoinBinding = ActivityJoinBinding.inflate(getLayoutInflater());
        setContentView(activityJoinBinding.getRoot());

        // 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        joinID = activityJoinBinding.joinIdText;
        joinEmail = activityJoinBinding.joinEmailText;
        joinPw = activityJoinBinding.joinPwText;
        joinPwCheck = activityJoinBinding.joinPwCheckText;
        joinButton = activityJoinBinding.joinButton;
        joinPwCheckWarningImage = activityJoinBinding.joinPwCheckWarningImage;
        joinPwCheckWarningText = activityJoinBinding.joinPwCheckWarningText;

        joinPwCheckFlag = 0; // 0 = 비밀번호가 비밀번호 확인과 같지 않음, 1 = 비밀번호가 비밀번호 확인과 같음.

        // 텍스트 변경 감지
        textWatcher =  new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // 텍스트가 변경된 후
            @Override
            public void afterTextChanged(Editable editable) {
                // 비밀번호가 비밀번호 확인이랑 같은 경우
                if(joinPw.getText().toString().equals(joinPwCheck.getText().toString())){
                    // 회원가입 확인 절차 진행 여부 flag 1로 변경.
                    joinPwCheckFlag = 1;
                    // 경고 이미지 체크 표시로 변경.
                    joinPwCheckWarningImage.setImageResource(R.drawable.check);
                    // 경고 이미지 배경색 투명색으로 변경.
                    joinPwCheckWarningImage.setBackgroundColor(Color.parseColor("#e6f0ff"));
                    // 경고 문구 변경.
                    joinPwCheckWarningText.setText("비밀번호가 맞았습니다!");
                    // 경고 문구색 초록색으로 변경.
                    joinPwCheckWarningText.setTextColor(Color.parseColor("#4CAF50"));
                    Log.e("password와 passwordCheck가 일치함",joinPw.getText().toString() + " " + joinPwCheck.getText().toString());
                }

                // 비밀번호가 비밀번호 확인이랑 다른 경우
                else{
                    // 회원가입 확인 절차 진행 여부 flag 0으로 변경.
                    joinPwCheckFlag = 0;
                    // 경고 이미지 경고 표시로 변경.
                    joinPwCheckWarningImage.setImageResource(android.R.drawable.stat_sys_warning);
                    // 경고 이미지 배경색 빨간색으로 변경.
                    joinPwCheckWarningImage.setBackgroundColor(Color.parseColor("#F44336"));
                    // 경고 문구 변경.
                    joinPwCheckWarningText.setText("비밀번호가 맞지 않습니다!");
                    // 경고 문구색 빨간색으로 변경.
                    joinPwCheckWarningText.setTextColor(Color.parseColor("#F44336"));
                    Log.e("password와 passwordCheck가 일치하지 않음",joinPw.getText().toString() + " " + joinPwCheck.getText().toString());
                }
            }
        };

        // 비밀번호 입력 필드와 비밀번호 확인 입력 필드 모두에 텍스트 감지 설정.
        // 둘 중 아무 입력 필드에 입력해도 텍스트 감지하여 일치 여부 확인 가능하도록 함.
        joinPw.addTextChangedListener(textWatcher);
        joinPwCheck.addTextChangedListener(textWatcher);

        // 회원가입 버튼 클릭 후 회원가입 처리 시작
        joinButton.setOnClickListener(view -> {
            // 사용자가 입력한 Id, Email, Pw
            String id = joinID.getText().toString();
            String email = joinEmail.getText().toString();
            String password = joinPw.getText().toString();

            // 이메일과 비밀번호가 비어있을 경우 경고
            if(email.equals("") && password.equals("")){
                warningDialog = new WarningDialog(JoinActivity.this, 1);
                warningDialog.show();
            }

            // 비밀번호가 6글자 이상 넘지 않으면 경고
            else if (password.length()<6){
                warningDialog = new WarningDialog(JoinActivity.this, 2);
                warningDialog.show();
            }

            // 비밀번호 확인 절차를 진행하지 않은 경우
            else if (joinPwCheckFlag == 0){
                warningDialog = new WarningDialog(JoinActivity.this, 3);
                warningDialog.show();
            }

            // 회원가입 진행
            else{
                // firebase에 이메일과 비밀번호를 이용하여 사용자 계정 생성 시작
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task는 실제로 회원가입 처리를 한 후의 결과값이다.
                        // 성공시
                        if(task.isSuccessful()){
                            // 방금 회원가입 처리가 된 user를 가져온다.
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            UserAccount userAccount = new UserAccount();
                            // userAccount에 방금 회원가입된 user의 정보들을 설정한다. (Id, Email, Password)
                            userAccount.setIdToken(firebaseUser.getUid());
                            userAccount.setUserId(id);
                            userAccount.setUserEmail(firebaseUser.getEmail());
                            userAccount.setUserPw(password);

                            // 생성된 userAccount를 user의 userUid의 child로 database에 삽입.
                            // 회원가입 시 사용자가 입력한 id의 child로 삽입하는 방법도 생각해보기
                            databaseReference.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);

                            // 회원가입 성공 다이얼로그
                            informationDialog = new InformationDialog(JoinActivity.this, 1);
                            informationDialog.show();

                            Log.e("회원가입 성공", userAccount.getIdToken()+" "+userAccount.getUserId()+" "+userAccount.getUserEmail()+" "+userAccount.getUserPw());

                        }
                        else {
                            // 회원가입 실패 다이얼로그
                            warningDialog = new WarningDialog(JoinActivity.this, 4);
                            warningDialog.show();

                            Log.e("회원가입 실패","");
                        }
                    }
                });
            }
        });
    }
}