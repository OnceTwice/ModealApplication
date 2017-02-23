package com.ff.modealapplication.app.ui.search;

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
import com.ff.modealapplication.app.core.service.SearchService;
import com.ff.modealapplication.app.core.util.GPSPreference;
import com.ff.modealapplication.app.ui.item.ItemDetailActivity;

import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {

    SearchResultListAdapter searchResultListAdapter =null;
    private  String result_search;
    public SearchResultActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        searchResultListAdapter = new SearchResultListAdapter(this);
        ListView listView = (ListView) findViewById(R.id.list_searchResultList);

        listView.setAdapter(searchResultListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultActivity.this, ItemDetailActivity.class);
                intent.putExtra("no", (Double.valueOf(((TextView) view.findViewById(R.id.send_no)).getText().toString())).longValue());
                intent.putExtra("shopNo", (Double.valueOf(((TextView) view.findViewById(R.id.send_shopNo)).getText().toString())).longValue());
                startActivity(intent);

            }
        });
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼

        SearchService.init(this);

        //searchactivity 에서 검색값 받아오기
        Intent intent = getIntent();
        result_search=intent.getExtras().getString("SEARCH");

        TextView textView = (TextView)findViewById(R.id.search_result);
        textView.setText(result_search);

        new ResultListAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==android.R.id.home){ // 뒤로가기 클릭시 searchActivity 종료
            this.finish();
            return true;
        } else if(id == R.id.action_button) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class ResultListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> call() throws Exception {
            SearchService searchService = new SearchService();
//            List<Map<String, Object>> list= new SearchService().resultList();

            List<Map<String, Object>> list= searchService.resultList((String) GPSPreference.getValue(getApplicationContext(), "latitude"), (String) GPSPreference.getValue(getApplicationContext(), "longitude"),result_search);

            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("SearchRunTime","Error :"+e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> maps) throws Exception {
            searchResultListAdapter.add(maps);
            super.onSuccess(maps);
        }
    }


}
