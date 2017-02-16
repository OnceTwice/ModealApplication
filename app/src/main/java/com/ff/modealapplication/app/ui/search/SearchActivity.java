package com.ff.modealapplication.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.SearchService;
import com.ff.modealapplication.app.core.domain.ItemVo;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchService searchService = new SearchService();
    SearchListArrayAdapter searchListArrayAdapter = null;
    private EditText search_edit;
    SearchDBArrayAdapter searchDBArrayAdapter = null;
    SearchDBManager searchDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        new SearchListAsyncTask().execute();

        searchDBManager = new SearchDBManager(getApplicationContext(), "SEARCH.db",null,1);

//        setListAdapter(searchListArrayAdapter);
//        ListActivity 상속 받지 않고 setListAdapter를 쓰기 위한 다른 방도
//        searchListArrayAdapter = new SearchListArrayAdapter(this);
        final ListView listView = (ListView) findViewById(R.id.list_search);
//        ArrayAdapter<String> lastSearchAdapter = new ArrayAdapter<String>(this, R.layout.row_search_list_image,)
//        listView.setAdapter(searchListArrayAdapter);
        searchDBArrayAdapter = new SearchDBArrayAdapter(this);

        listView.setAdapter(searchDBArrayAdapter);
        searchDBArrayAdapter.add(searchDBManager.PrintData());

        //listview 클릭시 editText에 클릭한 값 넣기
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("SEARCH", ((TextView)view.findViewById(R.id.text_search_list)).getText().toString());
                startActivity(intent);
            }
        });

        // ActionBar에 타이틀 변경
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼

        //검색 버튼 입력
        search_edit = (EditText) findViewById(R.id.search_button);

        if(search_edit==null || search_edit.getTextSize()==0){
          }else {

         }
            //엔터시 이동
            search_edit.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
                            final String SEARCH = search_edit.getText().toString();
                            Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                            intent.putExtra("SEARCH", SEARCH);
                            //search insert 최근검색어 추가.
                            searchDBManager.insert("insert into SEARCH_LIST (name) values('" + SEARCH + "');");
                            startActivity(intent);
                            return true;
                        }

                    return false;
                }
            });


        //텍스트 변경시 적용
        search_edit.addTextChangedListener(new TextWatcher() {
            //count 갯수만큼 글자들이 after길이만큼의 글자로 대치되려고 할 때 호출
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                SearchActivity.this.searchListArrayAdapter.getFilter().filter(s);
//                getSharedPreference();
                Toast.makeText(SearchActivity.this, "입력전",Toast.LENGTH_SHORT).show();
            }
            //count 갯수만큼 글자들로 대치되었을때 호출
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                SearchActivity.this.searchListArrayAdapter.getFilter().filter(s);
                Toast.makeText(SearchActivity.this, "입력중",Toast.LENGTH_SHORT).show();
            }
            //edittext 텍스트가 변경시 호출
            @Override
            public void afterTextChanged(Editable s) {
                Toast.makeText(SearchActivity.this, "입력후",Toast.LENGTH_SHORT).show();

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
            searchDBManager.insert("insert into SEARCH_LIST (name) values('" + SEARCH + "');");
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
            searchListArrayAdapter.add(itemVos);
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
