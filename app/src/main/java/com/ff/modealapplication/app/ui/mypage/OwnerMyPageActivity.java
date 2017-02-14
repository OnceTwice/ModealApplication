package com.ff.modealapplication.app.ui.mypage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private ImageView imageView;
    private Bitmap bitmap;

    private Button btnModify;

    private String imageURL;

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
        imageView = (ImageView) findViewById(R.id.imgVOwnerMyPageMarketImg);

        btnModify = (Button) findViewById(R.id.btnOwnerInformModify);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_owner_myPage); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        tvID.setText((String) LoginPreference.getValue(getApplicationContext(), "id"));
        tvPassword.setText((String) LoginPreference.getValue(getApplicationContext(), "password"));
        tvGender.setText((String) LoginPreference.getValue(getApplicationContext(), "gender"));
        tvLocation.setText((String) LoginPreference.getValue(getApplicationContext(), "location"));
        tvBirth.setText((String) LoginPreference.getValue(getApplicationContext(), "birth"));

        tvMarketName.setText((String) LoginPreference.getValue(getApplicationContext(), "name"));
        tvMarketAddress.setText((String) LoginPreference.getValue(getApplicationContext(), "address"));
        tvMarketPhoneNumber.setText((String) LoginPreference.getValue(getApplicationContext(), "phone"));
        tvMarketIntroduce.setText((String) LoginPreference.getValue(getApplicationContext(), "introduce"));
        imageURL = (String) LoginPreference.getValue(getApplicationContext(), "picture");

        /////////////////////////////// 이미지 세팅 ////////////////////////////////////
        //  안드로이드에서 네트워크 관련 작업을 할 때는
        //  반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 함
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageURL);    // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch(IOException ex) {

                }
            }
        };

        mThread.start();        // 웹에서 이미지를 가져오는 작업 스레드 실행.

        try {
            //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
            //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
            //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.
            mThread.join();

            //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
            //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.
            imageView.setImageBitmap(bitmap);
        } catch(InterruptedException e) {

        }
        ////////////////////////////////////////////////////////////////////////////////////

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
