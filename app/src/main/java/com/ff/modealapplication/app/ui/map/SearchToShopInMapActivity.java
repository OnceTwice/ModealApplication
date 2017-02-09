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
import com.ff.modealapplication.app.ui.join.JoinFragment;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class SearchToShopInMapActivity extends AppCompatActivity implements MapView.MapViewEventListener {

    Double longitude;
    Double latitude;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_to_shop_in_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map_select); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시


        mapView = new MapView(this);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view_SearchToShopInMap);
        container.addView(mapView);

        findViewById(R.id.fab_SearchToShopInMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent(SearchToShopInMapActivity.this, JoinFragment.class);

                returnIntent.putExtra("latitude", String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude));
                returnIntent.putExtra("longitude", String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude));

                finish();
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
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.537229, 127.005515), true);
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
