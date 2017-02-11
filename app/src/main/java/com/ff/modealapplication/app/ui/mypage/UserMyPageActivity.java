package com.ff.modealapplication.app.ui.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.modify.UserModifyActivity;

public class UserMyPageActivity extends AppCompatActivity {
    private TextView tvID;
    private TextView tvPassword;
    private TextView tvGender;
    private TextView tvLocation;
    private TextView tvBirth;

    private Button btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_page);

        tvID = (TextView) findViewById(R.id.tvUserMyPageID);
        tvPassword = (TextView) findViewById(R.id.tvUserMyPagePassword);
        tvGender = (TextView) findViewById(R.id.tvUserMyPageGender);
        tvLocation = (TextView) findViewById(R.id.tvUserMyPageLocation);
        tvBirth = (TextView) findViewById(R.id.tvUserMyPageBirth);
        btnModify = (Button) findViewById(R.id.btnUserInformModify);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_myPage); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        tvID.setText((String) LoginPreference.getValue(getApplicationContext(), "id"));
        tvPassword.setText((String) LoginPreference.getValue(getApplicationContext(), "password"));
        tvGender.setText((String) LoginPreference.getValue(getApplicationContext(), "gender"));
        tvLocation.setText((String) LoginPreference.getValue(getApplicationContext(), "location"));
        tvBirth.setText((String) LoginPreference.getValue(getApplicationContext(), "birth"));

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMyPageActivity.this, UserModifyActivity.class);
                startActivity(intent);
            }
        });
    }

    // 뒤로가기 클릭시 & 돋보기 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 뒤로가지 클릭시
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
