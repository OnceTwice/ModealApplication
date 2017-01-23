package com.ff.modealapplication.app.ui.Main;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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




    /****************************************************** 영조가 작성함 ***************************************************************/
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
     **************************************************** 리스너 클래스 정의 *******************************************************************
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
