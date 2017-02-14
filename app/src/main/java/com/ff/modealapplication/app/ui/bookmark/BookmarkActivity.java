package com.ff.modealapplication.app.ui.bookmark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.util.LoginPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private BookmarkItemList bookmarkItemList;
    private BookmarkShopList bookmarkShopList;
    private ListView listView_item;
    private ListView listView_shop;

    ArrayList<String> checkedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bookmark); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        // ItemList 생성
        bookmarkItemList = new BookmarkItemList(this);
        listView_item = (ListView) findViewById(R.id.bookmark_item_list);
        listView_item.setAdapter(bookmarkItemList);

        // ShopList 생성
        bookmarkShopList = new BookmarkShopList(this);
        listView_shop = (ListView) findViewById(R.id.bookmark_shop_list);
        listView_shop.setAdapter(bookmarkShopList);
        new BookmarkListAsyncTask().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToggleButton tb = (ToggleButton)view.findViewById(R.id.bookmark_item_delete);
        TextView tv = (TextView)view.findViewById(R.id.bookmark_item_text);
        tb.performClick();
        if (tb.isChecked()) {
            checkedValue.add(tv.getText().toString());
        } else if (!tb.isChecked()) {
            checkedValue.remove(tv.getText().toString());
        }
    }

    private class BookmarkListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            BookmarkService bookmarkService = new BookmarkService();
            List<Map<String, Object>> list = bookmarkService.bookmarkList((Long)LoginPreference.getValue(getApplicationContext(), "no"));
            return list;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            bookmarkItemList.add(list);
            bookmarkShopList.add(list);
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
//            super.onException(e);
        }
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
