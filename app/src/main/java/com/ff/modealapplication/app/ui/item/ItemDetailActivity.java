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
import com.ff.modealapplication.app.core.vo.ItemVo;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

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
            case R.id.button_delete: {
//            ItemListAsyncTask itemListAsyncTask = new ItemListAsyncTask();
//            itemListAsyncTask.execute();

                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }

            case R.id.button_hiding: {
                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }

            case R.id.button_modify: {
                Intent intent = new Intent(ItemDetailActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private class ItemDetailTask extends SafeAsyncTask<ItemVo> {
        @Override
        public ItemVo call() throws Exception {
            // getIntent().getStringExtra("no") ← 이전 액티비티에서 no값 받아옴 (이걸로 서버에 접근해서 해당 정보 가져오기 위해서...)
            String s = getIntent().getStringExtra("no");
            Log.w("********************", s + "???");
            return itemService.itemDetail(1L);
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.w("!!!!!!!!", "에러");
            Log.d("!!", "" + e);
            super.onException(e);
        }

        @Override // 성공하면 해당 매장명과 상품목록 출력
        protected void onSuccess(ItemVo itemVo) throws Exception {
            Log.w("!!!!!!!!", "성공");
            StringTokenizer tokens = new StringTokenizer(itemVo.getExpDate());
            String year = tokens.nextToken("/");
            String month = tokens.nextToken("/");
            String day = tokens.nextToken(" ");
            String hour = tokens.nextToken(":");
            String minute = tokens.nextToken(":");

            ((TextView)findViewById(R.id.item_list_clock)).setText(year + "년 " + month + "월 " + day + "일 " + hour + "시 " + minute + "분");
        }
    }
}
