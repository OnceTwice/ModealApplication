package com.ff.modealapplication.app.ui.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.modify.OwnerModifyActivity;

public class OwnerMyPageActivity extends AppCompatActivity {
    private TextView tvID;
    private TextView tvPassword;
    private TextView tvGender;
    private TextView tvLocation;
    private TextView tvBirth;

    private TextView tvMarketName;
    private TextView tvMarketAddress;
    private TextView tvMarketPhoneNumber;
    private TextView tvMarketIntroduce;
    private ImageView imgVMarketImg;

    private Button btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_my_page);

        tvID = (TextView) findViewById(R.id.tvOwnerMyPageID);
        tvPassword = (TextView) findViewById(R.id.tvOwnerMyPagePassword);
        tvGender = (TextView) findViewById(R.id.tvOwnerMyPageGender);
        tvLocation = (TextView) findViewById(R.id.tvOwnerMyPageLocation);
        tvBirth = (TextView) findViewById(R.id.tvOwnerMyPageBirth);

        tvMarketName = (TextView) findViewById(R.id.tvOwnerMyPageMarketName);
        tvMarketAddress = (TextView) findViewById(R.id.tvOwnerMyPageMarketAddress);
        tvMarketPhoneNumber = (TextView) findViewById(R.id.tvOwnerMyPageMarketPhoneNumber);
        tvMarketIntroduce = (TextView) findViewById(R.id.tvOwnerMyPageMarketIntroduce);
        imgVMarketImg = (ImageView) findViewById(R.id.imgVOwnerMyPageMarketImg);

        btnModify = (Button) findViewById(R.id.btnOwnerInformModify);

        tvID.setText((String) LoginPreference.getValue(getApplicationContext(), "id"));
        tvPassword.setText((String) LoginPreference.getValue(getApplicationContext(), "password"));
        tvGender.setText((String) LoginPreference.getValue(getApplicationContext(), "gender"));
        tvLocation.setText((String) LoginPreference.getValue(getApplicationContext(), "location"));
        tvBirth.setText((String) LoginPreference.getValue(getApplicationContext(), "birth"));

        tvMarketName.setText((String) LoginPreference.getValue(getApplicationContext(), "name"));
        tvMarketAddress.setText((String) LoginPreference.getValue(getApplicationContext(), "address"));
        tvMarketPhoneNumber.setText((String) LoginPreference.getValue(getApplicationContext(), "phone"));
        tvMarketIntroduce.setText((String) LoginPreference.getValue(getApplicationContext(), "introduce"));
        // 이미지 설정 해야함....

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_owner_myPage); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OwnerMyPageActivity.this, OwnerModifyActivity.class);
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
