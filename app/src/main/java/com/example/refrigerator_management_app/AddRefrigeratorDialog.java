package com.example.refrigerator_management_app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.refrigerator_management_app.databinding.DialogPlusBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRefrigeratorDialog extends Dialog {
    private DialogPlusBinding dialogPlusBinding;
    private DatabaseReference databaseReference;
    private RefrigeratorViewModel refrigeratorViewModel;
    private RefrigeratorViewModel viewModel;
    private EditText edit_refrigerator_name; // 냉장고 이름 입력 필드
    private Context context; // 가지고 올 Activity의 Comtext
    private WarningDialog warningDialog; // 경고 다이얼로그
    private int itemPos = -1; // 뷰모델 안에서의 냉장고 위치
    Button okButton, cancelButton; // ok 버튼, cancel 버튼
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogPlusBinding = DialogPlusBinding.inflate(getLayoutInflater());
        setContentView(dialogPlusBinding.getRoot());

        /* ---------- 초기화 ---------- */
        databaseReference = FirebaseDatabase.getInstance().getReference();
        edit_refrigerator_name = dialogPlusBinding.plusDialogEditText;
        okButton = dialogPlusBinding.pludDialogbuttonOk;
        cancelButton = dialogPlusBinding.plusDialogbuttonCancel;

        // 냉장고가 처음 추가되는 것이 아니라면 edit_refrigerator_name에 기존 냉장고의 이름이 전체 선택된 상태로 보여지게 함
        if(itemPos != -1) {
            String name = refrigeratorViewModel.getRefrigeratorName(itemPos);
            edit_refrigerator_name.setText(name);
            edit_refrigerator_name.selectAll();
        }

        /*
         * ---------- 버튼 리스너 설정 ---------- *
         * itemPos : -1 = 새로운 냉장고 추가, else = 기존 냉장고의 수정 및 삭제 (수정은 아직 미구현)
         */
        okButton.setOnClickListener(view -> {
            // 냉장고 이름이 비어있는 걍우 추가 불가
            if(edit_refrigerator_name.getText().toString().equals("")) {
                warningDialog = new WarningDialog(context, 5);
                warningDialog.show();
                return;
            }
            // 냉장고 이름이 중복되는 경우 추가 불가
            else if(refrigeratorViewModel.refrigerators.contains(edit_refrigerator_name.getText().toString())) {
                warningDialog = new WarningDialog(context, 6);
                warningDialog.show();
                return;
            }
            // 냉장고 추가
            else {
                Log.e("AddRefrigeratorDialog", edit_refrigerator_name.getText().toString() + " 추가");

                // 새로운 냉장고 추가
                if(itemPos == -1) {
                    // 파이어베이스에 냉장고 추가
                    // 파이어베이스에 추가하면 자동으로 뷰모델에 추가되어 사용자 화면에 보여진다.
                    databaseReference.child("UserAccount").child(userUid).child("냉장고").child(edit_refrigerator_name.getText().toString()).setValue("");

                    // 냉장, 냉동까지 생성
//                    databaseReference.child("UserAccount").child(userUid).child("냉장고").child(edit_refrigerator_name.getText().toString()).child("냉장").setValue("");
//                    databaseReference.child("UserAccount").child(userUid).child("냉장고").child(edit_refrigerator_name.getText().toString()).child("냉동").setValue("");
                }
                else {
                    String refrigeratorPastName = refrigeratorViewModel.getRefrigeratorName(itemPos); // 냉장고의 전 이름
                    String refrigeratorNewName = edit_refrigerator_name.getText().toString(); // 냉장고의 수정된 이름
                    refrigeratorViewModel.deleteRefrigerator(itemPos); // 뷰모델에서 기존 냉장고 삭제
                    DatabaseReference updateDatabaseReference;
                    updateDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference newPath = updateDatabaseReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorNewName); // 냉장고의 수정된 이름으로 DatabaseReference를 만든다.
                    DatabaseReference pastPath = databaseReference.child("UserAccount").child(userUid).child("냉장고").child(refrigeratorPastName); // 냉장고의 수정 전 이름의 DatabaseReference를 가져온다.
                    // pastPath에서 newPath로 데이터 이동, 파이어베이스에서 냉장고의 이름이 수정되는 것 처럼 작동한다.
                    // 이때 newPath의 DatabaseReference에 pastPath의 데이터가 추가되면서
                    // 자동으로 뷰모델의 addRefrigerator가 호출되어 사용자의 화면에도 냉장고의 이름이 수정되어 나타난다.
                    updateRefrigerator(newPath, pastPath);
                    pastPath.removeValue(); // 수정 전 냉장고는 삭제한다.
                }
            }
            dismiss();
        });

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });
    }

    // 파이어베이스 안에서의 냉장고 DatabaseReference 이동
    private void updateRefrigerator(DatabaseReference newPath, final DatabaseReference pastPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newPath.setValue(snapshot.getValue()).addOnCompleteListener(task -> {
                    // 성공
                    if(task.isComplete())
                        Log.d("updateRefrigerator", "Success update Users");
                        // 실패
                    else
                        Log.d("updateRefrigerator", "Fail update Users");
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        pastPath.addListenerForSingleValueEvent(valueEventListener);
    }

    public AddRefrigeratorDialog(Context context, RefrigeratorViewModel viewModel, int pos, String userUid) {
        super(context);
        this.context = context;
        this.refrigeratorViewModel = viewModel;
        this.itemPos = pos;
        this.userUid = userUid;
    }
}
