package com.ff.modealapplication.app.ui.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.SearchService;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private SearchService searchService = new SearchService();
    private EditText search_edit;
    ListView listView;

    ArrayAdapter<String> adapter;

    ArrayList<Map<String, String>> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // ActionBar에 타이틀 변경
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼

//        List<ItemVo> list = searchService.searchList();

        //검색 입력시 리스트 출력
        listView = (ListView) findViewById(R.id.list_search);
        search_edit = (EditText) findViewById(R.id.search_button);

        String test[] = {"1", "13", "2", "13", "15", "42", "16"};
        adapter = new ArrayAdapter<String>(this, R.layout.search_list, R.id.search_textView, test);
        listView.setAdapter(adapter);

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                SearchActivity.this.adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //엔터시 이동
        search_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
                    final String SEARCH = search_edit.getText().toString();
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("SEARCH", SEARCH);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //돋보기
        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) { // 뒤로가기 클릭시 searchActivity 종료
            this.finish();
            return true;
        } else if (id == R.id.action_button) {
            final String SEARCH = search_edit.getText().toString();
            Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
            intent.putExtra("SEARCH", SEARCH);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class SearchListAsyncTask extends SafeAsyncTask<List<ItemVo>> {
        @Override
        public List<ItemVo> call() throws Exception {
            List<ItemVo> list = searchService.searchList();
            return list;
        }

        @Override
        protected void onSuccess(List<ItemVo> itemVos) throws Exception {
            super.onSuccess(itemVos);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("SearchException :", "" + e);
            throw new RuntimeException(e);
        }
    }
}
