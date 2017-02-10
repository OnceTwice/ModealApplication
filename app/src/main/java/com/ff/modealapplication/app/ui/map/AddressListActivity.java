package com.ff.modealapplication.app.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MapsService;
import com.ff.modealapplication.app.core.vo.daumvo.AddressVo;
import com.ff.modealapplication.app.core.vo.daumvo.DaumItemVo;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {
    public static Activity FinishAddressListActivity;

    private AddressListArrayAdapter addressListArrayAdapter = null;

    MapsService mapsService = new MapsService();

    ListView listView = null;
    Intent intentAddr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FinishAddressListActivity = AddressListActivity.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        addressListArrayAdapter = new AddressListArrayAdapter(this);

        listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(addressListArrayAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_address_list); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 없애기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        intentAddr = new Intent(this.getIntent()); // 앞 액티비티에서 보낸 값을 받아온다
        Log.d("intent====>", ""+intentAddr.getStringExtra("addr"));
        new FetchAddressListAsyncTask().execute();
    }

    private class FetchAddressListAsyncTask extends SafeAsyncTask<AddressVo> {

        @Override
        public AddressVo call() throws Exception {
            return mapsService.fetchAddress("" + intentAddr.getStringExtra("addr")); // 받아온 값 처리을 서비스로 보냄
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("Error======>", "" + e);
            super.onException(e);
        }

        @Override
        protected void onSuccess(AddressVo addressVo) throws Exception {
            List<DaumItemVo> daumItemVos = new ArrayList<>();
            for (int i = 0; i < addressVo.getChannel().getItem().size(); i++) { // 검색결과를 리스트에 담음
                daumItemVos.add(i, addressVo.getChannel().getItem().get(i));
                Log.d(i + 1 + "번째", "" + daumItemVos.get(i));
            }
            super.onSuccess(addressVo);
            addressListArrayAdapter.add(daumItemVos); // 리스트 어댑터 사용
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 뒤로가지 클릭시
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
