package com.ff.modealapplication.app.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.service.map.MapApiConst;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import static com.ff.modealapplication.app.ui.map.AddressListActivity.FinishAddressListActivity;
import static com.ff.modealapplication.app.ui.map.SearchMapRangeActivity.FinishSearchMapRangeActivity;

public class SearchToMapActivity extends AppCompatActivity implements MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener {
    private MapView mapView;
    Double longitude;
    Double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_to_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_to_map); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        Intent intent = new Intent(this.getIntent());

        longitude = Double.valueOf(intent.getStringExtra("longitude"));
        latitude = Double.valueOf(intent.getStringExtra("latitude"));
        System.out.println("longitude="+longitude+", latitude=" + latitude);

        mapView = new MapView(this);
        mapView.setMapViewEventListener(this);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view_SearchToMap);


        container.addView(mapView);

        findViewById(R.id.fab_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPoint thisMapPoint = MapPoint.mapPointWithGeoCoord(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);
                MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, thisMapPoint, SearchToMapActivity.this, SearchToMapActivity.this);

                mapReverseGeoCoder.startFindingAddress();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 뒤로가지 클릭시
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
         Intent returnIntent = new Intent(this, SearchMapRangeActivity.class);

        returnIntent.putExtra("latitude", String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude));
        returnIntent.putExtra("longitude", String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude));
        returnIntent.putExtra("title", s);

        FinishAddressListActivity.finish();
        FinishSearchMapRangeActivity.finish();

        startActivity(returnIntent);

        finish();
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
