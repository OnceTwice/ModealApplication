package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.service.ItemService;

/**
 * Created by bit-desktop on 2017-01-25.
 */

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ItemService itemService = new ItemService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

  /*      // 삭제 버튼 클릭시
        findViewById(R.id.button_delete).setOnClickListener(this);

        // 수정 버튼 클릭시
        findViewById(R.id.button_modify).setOnClickListener(this);

        // 숨기기 버튼 클릭시
        findViewById(R.id.button_hiding).setOnClickListener(this);*/
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
}
