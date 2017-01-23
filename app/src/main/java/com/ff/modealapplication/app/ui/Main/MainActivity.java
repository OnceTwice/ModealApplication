package com.ff.modealapplication.app.ui.Main;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MainService;


import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {

    private MainListArrayAdapter mainListArrayAdapter =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListArrayAdapter = new MainListArrayAdapter(this);
        setListAdapter(mainListArrayAdapter);

        new MainListAsyncTask().execute();
    }

    private class MainListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>>{
        @Override
        public List<Map<String, Object>> call() throws Exception {
            MainService mainService = new MainService();
            List<Map<String, Object>> list= mainService.MainItemList();
            Log.d("--------",""+list);
            return list;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            Log.d("success test ->",""+list);
            mainListArrayAdapter.add(list);
            Log.d("123",""+list);
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :" , ""+e);
            throw new RuntimeException(e);
//            super.onException(e);
        }
    }
}
