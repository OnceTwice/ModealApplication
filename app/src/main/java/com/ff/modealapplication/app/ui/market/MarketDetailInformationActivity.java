package com.ff.modealapplication.app.ui.market;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MarketService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MarketDetailInformationActivity extends AppCompatActivity {
    private MarketService marketService = new MarketService();

    private ImageView imageView;
    private TextView tvName;
    private TextView tvRatingBar;
    private RatingBar ratingBar;
    private TextView tvAddress;
    private TextView tvIntroduce;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail_information);

        imageView = (ImageView) findViewById(R.id.imgVMarketDetailInformationImg);
        tvName = (TextView) findViewById(R.id.tvMarketDetailInformationName);
        ratingBar = (RatingBar) findViewById(R.id.rbOutput);
        tvRatingBar = (TextView) findViewById(R.id.tvRatingBar);
        tvAddress = (TextView) findViewById(R.id.tvMarketDetailInformationAddress);
        tvIntroduce = (TextView) findViewById(R.id.tvMarketDetailInformationIntroduce);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_marketDetail); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        Toast.makeText(getApplicationContext(), "MarketDetailInformationActivity.java 에서의 ShopNo : "+getIntent().getLongExtra("ShopNo", 0), Toast.LENGTH_SHORT).show();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRatingBar.setText("rating : " + rating);
            }
        });

        new MarketInformationAsyncTask().execute();
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

    private class MarketInformationAsyncTask extends SafeAsyncTask<Map<String, Object>> {
        @Override
        public Map<String, Object> call() throws Exception {
            Log.d("MarketInfncTask : ", "입갤");
            return marketService.marketInform(getIntent().getLongExtra("ShopNo", 0));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("MarketInformation == ", "서비스 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(final Map<String, Object> shopInformMap) throws Exception {
            super.onSuccess(shopInformMap);
            Log.d("MarketInfncTask : ", "성공!!!");

            /////////////////////////////// 이미지 세팅(URL로 가지고 올 때) ////////////////////////////////////
            //  안드로이드에서 네트워크 관련 작업을 할 때는
            //  반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 함
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(shopInformMap.get("picture").toString());    // URL 주소를 이용해서 URL 객체 생성

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

            tvName.setText(shopInformMap.get("name").toString());
//            tvAvgScore.setText(shopInformMap.get("").toString());
            tvAddress.setText(shopInformMap.get("address").toString());
            tvIntroduce.setText(shopInformMap.get("introduce").toString());

        }
    }
}
