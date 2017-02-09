package com.ff.modealapplication.app.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.List;

/**
 * Created by yeo on 2017-01-30.
 */

public class ItemModifyActivity extends AppCompatActivity implements View.OnClickListener {
    private ItemService itemService = new ItemService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_modify);

        // 수정 버튼 클릭시
        findViewById(R.id.button_modify).setOnClickListener(this);

        // 취소 버튼 클릭시
        findViewById(R.id.button_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_modify: {
                ItemListAsyncTask itemListAsyncTask = new ItemListAsyncTask();
                itemListAsyncTask.execute();

                Intent intent = new Intent(ItemModifyActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
            }

            case R.id.button_cancel: {
                // 누나 이건 그냥 finish() 해야해
                // 상품목록에서 수정 눌러서 수정페이지로 넘어올때 이전 액티비티를 없애고 온게 아니라서 그냥 피니시만 해도 이전 액티비티가 남아있어서 거기로 가 (170209/상욱수정)
//                Intent intent = new Intent(ItemModifyActivity.this, ItemActivity.class);
//                startActivity(intent);
                finish();
            }
        }
    }

    private class ItemListAsyncTask extends SafeAsyncTask<List<ItemVo>> {

        public List<ItemVo> call() throws Exception {
            EditText editText1 = (EditText) findViewById(R.id.item_name);
            Log.d("name : ", editText1.getText().toString());
            String item_name = editText1.getText().toString();

            EditText editText2 = (EditText) findViewById(R.id.ori_price);
            Log.d("ori_price : ", editText2.getText().toString());
            Long ori_price = Long.parseLong(editText2.getText().toString());

            EditText editText3 = (EditText) findViewById(R.id.count);
            Log.d("count : ", editText3.getText().toString());
            Long count = Long.parseLong(editText3.getText().toString());

            EditText editText4 = (EditText) findViewById(R.id.price);
            Log.d("price : ", editText4.getText().toString());
            Long price = Long.parseLong(editText4.getText().toString());

            EditText editText6 = (EditText) findViewById(R.id.discount);
            Log.d("discount : ", editText6.getText().toString());
            Long discount = Long.parseLong(editText6.getText().toString());

            List<ItemVo> list = itemService.itemModify(item_name, ori_price, count, price, "a12345", discount);
            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<ItemVo> itemVos) throws Exception {
//            super.onSuccess(itemVos);
        }
    }
}