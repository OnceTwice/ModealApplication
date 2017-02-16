package com.ff.modealapplication.app.ui.map;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MapsService;
import com.ff.modealapplication.app.core.service.map.MapApiConst;
import com.ff.modealapplication.app.core.domain.ShopVo;
import com.ff.modealapplication.app.ui.market.MarketDetailInformationActivity;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainMapActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.POIItemEventListener {
    String longitude;
    String latitude;
    String range = "" + 200;
    MapCircle circle;

    private HashMap<Integer, ShopVo> mTagItemMap = new HashMap<Integer, ShopVo>();
    private MapView mapView;
    private MapsService mapsService = new MapsService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_map); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        /****************** 맵 설정 *********************/
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setMapViewEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        RelativeLayout container = (RelativeLayout) findViewById(R.id.main_map_view);
        container.addView(mapView);

        Toast.makeText(getApplicationContext(), "기기에 따라 위치찾기가 지연될 수\n있습니다. 잠시만 기다려 주세요.", Toast.LENGTH_LONG).show();


        /****************** 단추 *********************/
        findViewById(R.id.map_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMapActivity.this, SearchMapRangeActivity.class);
                startActivity(intent);
            }
        });
    }

    /**********
     * 첫실행
     **********/
    @Override
    public void onMapViewInitialized(MapView mapView) {
//        mapView.setCurrentLocationTrackingMode(mapView.getCurrentLocationTrackingMode());
       // mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        //문제점 ==> 자리가 아직 덜잡혔는데 모든 과정을 진행함...
        MapPoint thisMapPoint = MapPoint.mapPointWithGeoCoord(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude, mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);
        MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, thisMapPoint, this, this);

        mapReverseGeoCoder.startFindingAddress();

    }

    /*********************
     * 좌표--> 주소
     ********************/
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        latitude = String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
        longitude = String.valueOf(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);
        ((TextView) findViewById(R.id.main_map_title)).setText(s);

        circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(Double.valueOf(latitude), Double.valueOf(longitude)),
                Integer.valueOf(range),
                Color.argb(25, 255, 0, 0),
                Color.argb(25, 255, 0, 0)
        );
        circle.setTag(1234);
        mapView.addCircle(circle);


        new FetchShopListAsyncTask().execute();
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    private class FetchShopListAsyncTask extends SafeAsyncTask<List<ShopVo>> {

        @Override
        public List<ShopVo> call() throws Exception {

            Log.d("range =====>", "" + range);
            Log.d("latitude =====>", "" + latitude);
            Log.d("longitude =====>", "" + longitude);
            List<ShopVo> list = mapsService.fetchShopList(range, longitude, latitude);
            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("------->", "fail:" + e);
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<ShopVo> shopVos) throws Exception {
            for (int i = 0; i < shopVos.size(); i++) {
                ShopVo myShopVo = shopVos.get(i);
                shopVos.set(i, myShopVo);
                Log.d("--usersVo[" + (i + 1) + "번째]-->", "" + shopVos.get(i));
                //------------------풍선 생성---------------------------------
                showResult(shopVos);
                //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);//화면이동
            }

            super.onSuccess(shopVos);
        }
    }

    /***********************
     * 마크 세팅
     ***********************/
    private void showResult(List<ShopVo> shopVoList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < shopVoList.size(); i++) {
            ShopVo shopVo = shopVoList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(shopVoList.get(i).getName());
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(shopVoList.get(i).getLatitude(), shopVoList.get(i).getLongitude());
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), shopVo);
        }
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }

    }

    /**********************
     * 말풍선 터치 이벤트
     **********************/
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Toast.makeText(getApplicationContext(), "터치 이벤트 들어옴@@@@@@@@@@@@@@@", Toast.LENGTH_LONG).show();
        ShopVo shopVo = mTagItemMap.get(mapPOIItem.getTag());

        StringBuilder sb = new StringBuilder();
        sb.append("title=").append(shopVo.getName()).append("\n");

        Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MarketDetailInformationActivity.class);
        startActivity(intent);
    }

    /*************************
     * 마커 어댑터 시작
     *************************/
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) {
                return null;
            }
            ShopVo shopVo = mTagItemMap.get(poiItem.getTag());
            if (shopVo == null) {
                return null;
            }
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(shopVo.getName());
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            textViewDesc.setText(shopVo.getAddress());
            imageViewBadge.setImageDrawable(createDrawableFromUrl(shopVo.getPicture()));
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
            return null;
        }
    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    /********************
     * 마커 어댑터 끝
     *******************/


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    @Deprecated
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

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
