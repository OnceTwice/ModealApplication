package com.ff.modealapplication.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MainService;
import com.ff.modealapplication.app.core.util.GPSPreference;
import com.ff.modealapplication.app.ui.item.ItemDetailActivity;

import java.util.List;
import java.util.Map;

public class MainListFragment extends Fragment implements AdapterView.OnItemClickListener {

    MainListArrayAdapter mainListArrayAdapter = null;
    ListView listView = null;

    public MainListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mFragment = inflater.inflate(R.layout.fragment_main_list, container, false);
//        mainListArrayAdapter = new MainListArrayAdapter(mFragment.getContext());
//        mainListArrayAdapter.notifyDataSetChanged();
//        listView = (ListView) mFragment.findViewById(list);
//        listView.setAdapter(mainListArrayAdapter);
//        listView.setOnItemClickListener(this);

        new MainListAsyncTask().execute(); // 뒤로가기해도 뜨기 위해선 onCreate가 아닌 onCreateView에 넣어야함
        // onCreateView가 뷰가 만들어질때마다 이루어지는 것 같으므로... 뒤로가기해서 이 프래그먼트로 돌아올때 뷰를 다시 만드므로...?
        return mFragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), ItemDetailActivity.class);
        intent.putExtra("no", ((TextView) view.findViewById(R.id.send_no)).getText().toString());
        startActivity(intent);
    }

    private class MainListAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            MainService mainService = new MainService();
            List<Map<String, Object>> list = mainService.MainItemList((String)GPSPreference.getValue(getContext(), "latitude"), (String)GPSPreference.getValue(getContext(), "longitude"), (int)GPSPreference.getValue(getContext(), "range")); // 위도, 경도, 반경
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
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


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

                Toast.makeText(getContext(), "Last Known Location : " + "Latitude : " + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        Toast.makeText(getContext(), "위치 확인이 시작되었습니다. 잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
