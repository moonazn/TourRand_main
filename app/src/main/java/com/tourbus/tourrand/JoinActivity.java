package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {

    private EditText idEditText;
    private EditText pwEditText;
    private EditText nicknameEditText;
    private TextView idCheckInfo;
    private TextView nicknameCheckInfo;
    private Button idCheckBtn;
    private Button joinBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join); // XML 레이아웃 파일명

        idEditText = findViewById(R.id.idEditText);
        pwEditText = findViewById(R.id.pwEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        idCheckInfo = findViewById(R.id.idCheckInfo);
        nicknameCheckInfo = findViewById(R.id.nicknameCheckInfo);
        idCheckBtn = findViewById(R.id.idCheckBtn);
        joinBtn = findViewById(R.id.joinBtn);

        // 아이디 중복 확인 버튼 클릭 리스너
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredId = idEditText.getText().toString().trim();
                if (isIdDuplicated(enteredId)) {
                    idCheckInfo.setText("중복된 아이디입니다.");
                    idCheckInfo.setTextColor(getResources().getColor(R.color.red)); // red 컬러를 정의하세요
                    idCheckInfo.setVisibility(View.VISIBLE);
                } else {
                    idCheckInfo.setText("사용 가능한 아이디입니다.");
                    idCheckInfo.setTextColor(getResources().getColor(R.color.blue)); // blue 컬러를 정의하세요
                    idCheckInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        // 닉네임 입력 필드 변화 감지
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 이곳은 필요 없으므로 비워둡니다.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredNickname = s.toString().trim();
                if (isNicknameValid(enteredNickname)) {
                    nicknameCheckInfo.setText("사용 가능한 닉네임입니다.");
                    nicknameCheckInfo.setTextColor(getResources().getColor(R.color.blue)); // blue 컬러를 정의하세요
                    nicknameCheckInfo.setVisibility(View.VISIBLE);
                } else {
                    nicknameCheckInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 이곳은 필요 없으므로 비워둡니다.
            }
        });

        // 가입 완료 버튼 클릭 리스너
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();
                String nickname = nicknameEditText.getText().toString().trim();

                if (id.isEmpty() || pw.isEmpty() || nickname.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isIdDuplicated(id)) {
                    Toast.makeText(JoinActivity.this, "아이디가 중복되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isNicknameValid(nickname)) {
                    Toast.makeText(JoinActivity.this, "닉네임이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 여기서 회원가입 요청을 서버에 보낼 수 있습니다.
                Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish(); // 액티비티 종료
            }
        });
    }

    // 아이디 중복 확인 (서버 연동이 없으므로 예시로 간단한 체크 구현)
    private boolean isIdDuplicated(String id) {
        // 이곳에 실제 서버와의 중복 확인 로직을 넣을 수 있습니다.
        // 여기서는 단순히 "user"라는 아이디가 중복된다고 가정합니다.
        return id.equalsIgnoreCase("user");
    }

    // 닉네임 유효성 검사 (간단한 예시)
    private boolean isNicknameValid(String nickname) {
        // 닉네임은 유효하다고 가정합니다.
        return true;
    }
}
