package com.ff.modealapplication.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.SearchService;

import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {

    SearchService searchService;
    SearchResultListAdapter searchResultListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼

        //searchactivity 에서 검색값 받아오기
        Intent intent = getIntent();
        String result_search=intent.getExtras().getString("SEARCH");

        TextView textView = (TextView)findViewById(R.id.search_result);
        textView.setText(result_search);

        new ResultListAsyncTask().execute();

    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //돋보기
        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }*/

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
            List<Map<String, Object>> list= searchService.resultList();
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> maps) throws Exception {
            searchResultListAdapter.add(maps);
//            super.onSuccess(maps);
        }
    }


}
