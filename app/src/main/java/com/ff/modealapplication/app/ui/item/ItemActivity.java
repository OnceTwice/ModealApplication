package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

        // ListView 생성 후 세팅?????????????????
        itemListArrayAdapter = new ItemListArrayAdapter(this);
        ListView listView = (ListView) findViewById(R.id.item_list);
        listView.setAdapter(itemListArrayAdapter);

        new ItemListTask().execute(); // 아래 ItemListTask 클래스 실행

        // [+] 버튼 클릭시
        findViewById(R.id.button_item_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, ItemInsertActivity.class); // ItemInsertActivity 클래스를 실행
                startActivity(intent);
            }
        });

//        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = getLayoutInflater().inflate(R.layout.item_list_row, null, false);
        // 수정 버튼 클릭시
        view.findViewById(R.id.button_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, ItemModifyActivity.class);
                startActivity(intent);
            }
        });
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

        @Override // 성공하면 상품하나 추가 됨
        protected void onSuccess(List<Map<String, Object>> itemList) throws Exception {
            itemListArrayAdapter.add(itemList);
        }
    }
}
