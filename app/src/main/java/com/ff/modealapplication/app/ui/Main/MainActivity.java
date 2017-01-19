package com.ff.modealapplication.app.ui.Main;

import android.app.ListActivity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MainService;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.List;

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

    private class MainListAsyncTask extends SafeAsyncTask<List<ItemVo>>{
        @Override
        public List<ItemVo> call() throws Exception {
            MainService mainService = new MainService();
            List<ItemVo> list= mainService.MainItemList();
            Log.d("------!!!!",""+list);
            return list;
        }

        @Override
        protected void onSuccess(List<ItemVo> itemVos) throws Exception {
            mainListArrayAdapter.add(itemVos);
            super.onSuccess(itemVos);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("-Main Exception error :" , ""+e);
            throw new RuntimeException(e);
//            super.onException(e);
        }
    }
}
