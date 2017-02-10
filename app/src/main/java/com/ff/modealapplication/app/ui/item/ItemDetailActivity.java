package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by bit-desktop on 2017-01-25.
 */

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ItemService itemService = new ItemService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_item_detail); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);                // 뒤로가기 화살표 표시

        new ItemDetailTask().execute();

  /*      // 삭제 버튼 클릭시
        findViewById(R.id.button_delete).setOnClickListener(this);

        // 수정 버튼 클릭시
        findViewById(R.id.button_modify).setOnClickListener(this);

        // 숨기기 버튼 클릭시
        findViewById(R.id.button_hiding).setOnClickListener(this);*/
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_delete_item: {
//            ItemListAsyncTask itemListAsyncTask = new ItemListAsyncTask();
//            itemListAsyncTask.execute();

                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }

            case R.id.button_hiding_item: {
                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }

            case R.id.button_modify_item: {
                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private class ItemDetailTask extends SafeAsyncTask<Map<String, Object>> {
        @Override
        public Map<String, Object> call() throws Exception {
            // getIntent().getStringExtra("no") ← 이전 액티비티에서 no값 받아옴 (이걸로 서버에 접근해서 해당 정보 가져오기 위해서...)
            return itemService.itemDetail(Long.parseLong(getIntent().getStringExtra("no")));
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.e("Error!!!!!!!!!!!! : ", e + "");
            super.onException(e);
        }

        @Override // 성공하면 해당 매장명과 상품목록 출력
        protected void onSuccess(Map<String, Object> itemMap) throws Exception {
            StringTokenizer tokens = new StringTokenizer(itemMap.get("expDate").toString(), "-/: ");
            String year = tokens.nextToken();
            String month = tokens.nextToken();
            String day = tokens.nextToken();
            String hour = tokens.nextToken();
            String minute = tokens.nextToken();

            ((TextView)findViewById(R.id.item_detail_clock)).setText(year + "년 " + month + "월 " + day + "일 " + hour + "시 " + minute + "분");
            ((TextView)findViewById(R.id.item_detail_name)).setText(itemMap.get("name").toString());
            ((TextView)findViewById(R.id.item_detail_ori_price)).setText(itemMap.get("oriPrice") + "");
            ((TextView)findViewById(R.id.item_detail_price)).setText(itemMap.get("price") + "");
            ((TextView)findViewById(R.id.item_detail_shop_name)).setText(itemMap.get("shopName").toString());
        }
    }
}
