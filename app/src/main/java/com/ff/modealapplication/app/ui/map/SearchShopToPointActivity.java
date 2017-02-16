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
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class SearchShopToPointActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener{
    Double longitude;
    Double latitude;
    String range;
    MapCircle circle;
    private HashMap<Integer, ShopVo> mTagItemMap = new HashMap<Integer, ShopVo>();
    private MapsService mapsService = new MapsService();
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shop_to_point);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_shop_to_point); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시
//       -------------------------------------------------------
        Intent intent = new Intent(this.getIntent());
        longitude = Double.valueOf(intent.getStringExtra("longitude")); // 이전 액티비티에서 받아온 경도
        latitude = Double.valueOf(intent.getStringExtra("latitude")); // 받아온 위도
        range = intent.getStringExtra("range"); // 받아온 반경

        ((TextView) findViewById(R.id.search_shop_to_point)).setText(""+intent.getStringExtra("title")); // 받아온 주소값 툴바명으로 사용
        //----------------------------------------------------------------
        mapView = new MapView(this); // 다음 맵뷰 생성
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY); // 다음 맵뷰 키값
//------------------------리스너 // 뭔지 잘 모르겠음 꼭 넣어줘야 하는거 같음
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter()); // 마커 어댑터..?

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view_point);

        circle = new MapCircle( // 원 그리기
                MapPoint.mapPointWithGeoCoord(latitude, longitude), // 중앙지점
                Integer.valueOf(range), // 반경
                Color.argb(25, 255, 0, 0), // 테두리색
                Color.argb(25, 255, 0, 0) // 색칠되는 색
        );
        circle.setTag(1234);
        mapView.addCircle(circle); // 맵뷰에 원 추가

        container.addView(mapView); // 레이아웃에 맵뷰 넣기

        new FetchShopListAsyncTask().execute();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) { // 마커 클릭시 해당 매장으로 이동
        ShopVo shopVo = mTagItemMap.get(mapPOIItem.getTag());

        StringBuilder sb = new StringBuilder();
        sb.append("ShopTitle=").append(shopVo.getName()).append("\n");

        Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MarketDetailInformationActivity.class); // 마커 해당 매장으로 이동
        startActivity(intent);
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    private class FetchShopListAsyncTask extends SafeAsyncTask<List<ShopVo>> {
        @Override
        public List<ShopVo> call() throws Exception {
            List<ShopVo> list = mapsService.fetchShopList(range, String.valueOf(longitude), String.valueOf(latitude)); // 해당 원 안에 있는 상점을 서버에서 가져오기
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

                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);//화면이동
            }
            super.onSuccess(shopVos);
        }
    }

    private void showResult(List<ShopVo> shopVoList) { // 풍선 생성
        MapPointBounds mapPointBounds = new MapPointBounds();
        for (int i = 0; i < shopVoList.size(); i++) {
            ShopVo shopVo = shopVoList.get(i);

            MapPOIItem poiItem = new MapPOIItem(); // 풍선
            poiItem.setItemName(shopVoList.get(i).getName()); // 주소 넣음
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(shopVoList.get(i).getLatitude(), shopVoList.get(i).getLongitude()); // 맵포인트에 위도, 경도 넣음
            poiItem.setMapPoint(mapPoint); // 풍선에 위도, 경도가 들어간 맵포인트 넣음
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

        //mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
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

    /*************************
     * 마커 어댑터 시작
     *************************/
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) { // 풍선 만들기
            if (poiItem == null) {
                return null;
            }
            ShopVo shopVo = mTagItemMap.get(poiItem.getTag());
            if (shopVo == null) {
                return null;
            }
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge); // 매장사진
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title); // 매장명
            textViewTitle.setText(shopVo.getName());
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc); // 주소
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
}
