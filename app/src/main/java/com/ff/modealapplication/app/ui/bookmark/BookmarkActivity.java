package com.ff.modealapplication.app.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.item.ItemDetailActivity;
import com.ff.modealapplication.app.ui.market.MarketDetailInformationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private BookmarkItemList bookmarkItemList;
    private BookmarkShopList bookmarkShopList;
    private ListView listView_item;
    private ListView listView_shop;

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
        listView_item.setOnItemClickListener(this);

        // ShopList 생성
        bookmarkShopList = new BookmarkShopList(this);
        listView_shop = (ListView) findViewById(R.id.bookmark_shop_list);
        listView_shop.setAdapter(bookmarkShopList);
        listView_shop.setOnItemClickListener(this);

        new BookmarkListAsyncTask().execute();
    }

    @Override
    protected void onRestart() { // 물품 클릭시 즐겨찾기 해제시 즐겨찾기 리스트 갱신을 위해서
        super.onRestart();
        new BookmarkListAsyncTask().execute();
        bookmarkItemList.clear();
        bookmarkItemList.notifyDataSetChanged();
        bookmarkShopList.clear();
        bookmarkShopList.notifyDataSetChanged();
    }

    // 즐겨찾기한 상품/매장으로 이동
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.book_item: { // 상품으로 이동
                Log.i("이동할 아이템의 NO", ((TextView) view.findViewById(R.id.send_no_item)).getText().toString());
                Intent intent = new Intent(this, ItemDetailActivity.class);
                intent.putExtra("no", (Double.valueOf(((TextView) view.findViewById(R.id.send_no_item)).getText().toString())).longValue());
                intent.putExtra("shopNo", (Double.valueOf(((TextView) view.findViewById(R.id.send_no_shop)).getText().toString())).longValue());
                startActivity(intent);
                onStop(); // 물품 클릭시 즐겨찾기 해제시 즐겨찾기 리스트 갱신을 위해서
                break;
            }
            case R.id.book_shop: { // 매장으로 이동
                Log.i("이동할 매장의 NO", ((TextView) view.findViewById(R.id.send_no_shop)).getText().toString());
                Intent intent = new Intent(this, MarketDetailInformationActivity.class);
                intent.putExtra("ShopNo", (Double.valueOf(((TextView) view.findViewById(R.id.send_no_shop)).getText().toString())).longValue());
                startActivity(intent);
                onStop(); // 물품 클릭시 즐겨찾기 해제시 즐겨찾기 리스트 갱신을 위해서
                break;
            }
        }
    }

    // 즐겨찾기 목록 띄우기
    private class BookmarkListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            BookmarkService bookmarkService = new BookmarkService();
            List<Map<String, Object>> list = bookmarkService.bookmarkList((Long) LoginPreference.getValue(getApplicationContext(), "no"));
            return list;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> shopList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("iname") != null) {
                    itemList.add(list.get(i));
                } else if (list.get(i).get("sname") != null) {
                    shopList.add(list.get(i));
                }
            }
            bookmarkItemList.add(itemList);
            bookmarkShopList.add(shopList);
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("Exception error :", "" + e);
            throw new RuntimeException(e);
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