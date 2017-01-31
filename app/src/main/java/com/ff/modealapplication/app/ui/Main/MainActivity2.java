package com.ff.modealapplication.app.ui.main;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MainService;
import com.ff.modealapplication.app.ui.login.LoginActivity;
import com.ff.modealapplication.app.ui.search.SearchActivity;

import java.util.List;
import java.util.Map;

public class MainActivity2 extends ListActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private MainListArrayAdapter mainListArrayAdapter = null;
    AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ListActivity에서 setSupportActionBar를 쓰기 위해서 한것들... (170123/상욱추가)
        // ListActivity를 상속 받지 않으면 이것을 쓸 필요 없습니다
        AppCompatCallback callback = new AppCompatCallback() {
            @Override
            public void onSupportActionModeStarted(ActionMode mode) {
            }

            @Override
            public void onSupportActionModeFinished(ActionMode mode) {
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                return null;
            }

        };
        delegate = AppCompatDelegate.create(this, callback);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_main);

        // 위에꺼는 안쓴다면 밑에 있는 delegate는 전부 지워주세요
        // 툴바 추가 (170123/상욱추가)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀에 있는 앱이름 숨기기

        // 네비게이션 추가(170123/상욱추가)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 로그인 버튼 클릭시 액티비티 이동!!!
        ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button).setOnClickListener(this);


        mainListArrayAdapter = new MainListArrayAdapter(this);
        setListAdapter(mainListArrayAdapter);

        new MainListAsyncTask().execute();
    }

    // onClick 매번 생성 하기 없이 한꺼번에 하기 위해서 (170123/상욱추가)
    // case 추가하면서 온클릭 사용하세요~
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    // 단순히 액션바부터 네비게이션 돋보기 추가하기 위해서는 여기서 부터~~~
    // 뒤로가기 버튼 누를때... (170123/상욱추가)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 액션바 맨 오른쪽 돋보기 추가 (170123/상욱추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        delegate.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 돋보기 눌렀을때 효과... (170123/상욱추가)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    // 네비게이션 (170123/상욱추가)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myPage) {

        } else if (id == R.id.nav_bookmark) {

        } else if (id == R.id.nav_setup) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // 여기까지 복붙~~

    private class MainListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            MainService mainService = new MainService();
            List<Map<String, Object>> list = mainService.MainItemList();
            return list;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            mainListArrayAdapter.add(list);
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
//            super.onException(e);
        }
    }


    /******************************************************
     * 영조가 작성함
     ***************************************************************/
    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 0;
        float minDistance = 0;

        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 네트워크를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : " + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "위치 확인이 시작되었습니다. 잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
    }

    /**
     * *************************************************** 리스너 클래스 정의 *******************************************************************
     */
    private class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String msg = "Latitude : " + latitude + "\nLongitude:" + longitude;
            Log.i("GPSListener", msg);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
    /**************************************************************************************************************************************/
}
