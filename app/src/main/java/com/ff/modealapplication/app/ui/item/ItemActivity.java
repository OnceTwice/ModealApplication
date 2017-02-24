package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;

import java.util.List;
import java.util.Map;

/**
 * Created by bit-desktop on 2017-01-19.
 */

public class ItemActivity extends AppCompatActivity implements AdapterView.OnItemClickListener { // AppCompatActivity 상속해줘야 main 화면 출력됨
    private ItemListArrayAdapter itemListArrayAdapter = null;
    private ListView listView = null;

    @Override //
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);                                                        // 첫화면인 item_list.xml을 출력

        // 헤더부분 ( 아래 4줄 toolbar 세트로 입력해줘야함)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_item_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);                                      // ← 표시 (뒤로가기 id는 home)

        // ListView 생성
        itemListArrayAdapter = new ItemListArrayAdapter(ItemActivity.this);
        listView = (ListView) findViewById(R.id.item_list);
//        LinearLayout linearLayout = (LinearLayout) View.inflate(this,R.layout.item_list_row ,null);
        listView.setAdapter(itemListArrayAdapter);

        // 리스트뷰 아이템 클릭 이벤트 처리
        listView.setOnItemClickListener(ItemActivity.this);

        new ItemListTask().execute();                                                               // 아래 ItemListTask 클래스 실행
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new ItemListTask().execute();
        itemListArrayAdapter.clear();
        itemListArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
        intent.putExtra("no", Long.valueOf(((TextView)view.findViewById(R.id.send_no)).getText().toString()));
        intent.putExtra("shopNo", getIntent().getLongExtra("shopNo", -1));
        startActivity(intent);
    }

    // 헤더부분 옵션메뉴: ← (뒤로가기), + (상품추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plus, menu);
        return true;
    }

    // ←(뒤로가기) 또는 +(상품추가) 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.plus_button) {
            Intent intent = new Intent(ItemActivity.this, ItemInsertActivity.class);              // ItemInsertActivity 클래스를 실행
            startActivity(intent);                                                                  // 이동
        } else if (item.getItemId() == android.R.id.home) {                                       // 뒤로가기 버튼 실행
            finish();
        }
        return super.onOptionsItemSelected(item);                                                 // return true와 동일
    }

    // SafeAsyncTask는 안드로이드에서 이클립스(서버)로 접근마다 써줘야함
    private class ItemListTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            return new ItemService().itemList(getIntent().getLongExtra("shopNo", -1));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {                        // 에러나면 Exception 발생
            Log.d("!!!!!!!!!!!!!", "" + e);
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> itemList) throws Exception {          // 성공하면 해당 매장명과 상품목록 출력
            ((TextView) findViewById(R.id.shop_name)).setText(itemList.get(0).get("shopName").toString());
            itemListArrayAdapter.add(itemList);                                                  // 목록에 상품 추가
        }
    }
}
