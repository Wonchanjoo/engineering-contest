package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.example.refrigerator_management_app.databinding.DialogWarningBinding;

public class WarningDialog extends Dialog {

    private DialogWarningBinding dialogWarningBinding; // WarningDialog binding
    private Context context; // 가지고 올 Activity의 Context
    private ImageView warningText; // 다이얼로그의 경고 문구
    private Button okButton; // ok 버튼
    private int myMode; // mode에 따라 다이얼로그의 경고 문구가 바뀌도록 함.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogWarningBinding = DialogWarningBinding.inflate(getLayoutInflater());
        setContentView(dialogWarningBinding.getRoot());

        // 초기화
        warningText = dialogWarningBinding.warningDialogText;
        okButton = dialogWarningBinding.warningDialogbuttonOk;

        switch (myMode){
            // 이메일과 비밀번호가 비어있는 경우
            case 1:
                warningText.setImageResource(R.drawable.no_empty_email_password);
                Log.e("WarningDialog의 ","mode : 1");
                break;
            // 비밀번호가 6글자를 넘지 않는 경우
            case 2:
                warningText.setImageResource(R.drawable.write_six_password);
                Log.e("WarningDialog의 ","mode : 2");
                break;
            // 비밀번호 확인 절차를 진행하지 않은 경우
            case 3:
                warningText.setImageResource(R.drawable.do_pw_pheck);
                Log.e("WarningDialog의 ","mode : 3");
                break;
            // 회원가입 실패시
            case 4:
                warningText.setImageResource(R.drawable.retry_register);
                Log.e("WarningDialog의 ","mode : 4");
                break;
            // 냉장고 이름이 비어있는 경우
            case 5:
                warningText.setImageResource(R.drawable.no_empty_refrigerator);
                Log.e("WarningDialog의 ","mode : 5");
                break;
            // 냉장고 이름이 중복되는 경우
            case 6:
                warningText.setImageResource(R.drawable.already_exist_id);
                Log.e("WarningDialog의 ","mode : 6");
                break;
            // 로그인에 실패한 경우
            case 7:
                warningText.setImageResource(R.drawable.login_failed);
                Log.e("WarningDialog의 ","mode : 7");
                break;

            // mode가 설정되어있지 않는 경우
            default:
                Log.e("WarningDialog의 ","mode 설정이 되어있지 않음");
                break;
        }

        // okButton 클릭
        okButton.setOnClickListener(view -> {
            Log.e("warningDialog의 ","OK 버튼이 눌림");
            dismiss();
        });

    }

    // WarningDialog의 생성자
    public WarningDialog(Context context, int mode) {
        super(context);
        this.context = context;
        this.myMode = mode;
    }
}
