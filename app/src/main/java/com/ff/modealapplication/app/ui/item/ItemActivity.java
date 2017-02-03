package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class ItemActivity extends AppCompatActivity { // AppCompatActivity 상속해줘야 main 화면 출력됨

    private ItemListArrayAdapter itemListArrayAdapter = null;

    @Override // Alt + Insert 누르고 오버라이드 메서드 클릭 후 onCreate 추가
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list); // 첫화면인 item_list.xml을 출력

        // 헤더부분 ( 아래 4줄은 세트로 입력해줘야함)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_item_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ← 표시 (뒤로가기 id는 home)

        // ListView 생성 후 세팅?
        itemListArrayAdapter = new ItemListArrayAdapter(this);
        ListView listView = (ListView) findViewById(R.id.item_list);
        listView.setAdapter(itemListArrayAdapter);

        new ItemListTask().execute(); // 아래 ItemListTask 클래스 실행

//        //LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = getLayoutInflater().inflate(R.layout.item_list_row, null, false);
//        // 수정 버튼 클릭시
//        view.findViewById(R.id.button_modify).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ItemActivity.this, ItemModifyActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // 삭제 버튼 클릭시
//        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ItemActivity.this, ItemActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        // 상품 클릭시
//        findViewById(R.id.item_list_row).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ItemActivity.this, ItemDetailActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    // 헤더부분 옵션메뉴: ←, +
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plus, menu);
        return true;
    }

    // ← 또는 + 버튼 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.plus_button) {
            Intent intent = new Intent(ItemActivity.this, ItemInsertActivity.class); // ItemInsertActivity 클래스를 실행
            startActivity(intent); // 이동
        } else if (item.getItemId() == android.R.id.home) { // 뒤로가기 버튼 실행
            finish();
        }
        return super.onOptionsItemSelected(item); // return true와 동일
    }

    private class ItemListTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            return new ItemService().itemList(1L);
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.d("!!", "" + e);
            super.onException(e);
        }

        @Override // 성공하면 해당 매장명과 상품목록 출력
        protected void onSuccess(List<Map<String, Object>> itemList) throws Exception {
            ((TextView)findViewById(R.id.shop_name)).setText(itemList.get(0).get("shopName").toString());
            itemListArrayAdapter.add(itemList);
        }
    }
}
