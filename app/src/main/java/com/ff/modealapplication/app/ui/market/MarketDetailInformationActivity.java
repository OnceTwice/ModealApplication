package com.ff.modealapplication.app.ui.market;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MarketService;

import java.util.Map;

public class MarketDetailInformationActivity extends AppCompatActivity {
    private MarketService marketService = new MarketService();

    private TextView tvName;
    private TextView tvRatingBar;
    private RatingBar ratingBar;
    private TextView tvAddress;
    private TextView tvIntroduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail_information);

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
        protected void onSuccess(Map<String, Object> shopInformMap) throws Exception {
            super.onSuccess(shopInformMap);
            Log.d("MarketInfncTask : ", "성공!!!");

            tvName.setText(shopInformMap.get("name").toString());
//            tvAvgScore.setText(shopInformMap.get("").toString());
            tvAddress.setText(shopInformMap.get("address").toString());
            tvIntroduce.setText(shopInformMap.get("introduce").toString());

        }
    }
}
