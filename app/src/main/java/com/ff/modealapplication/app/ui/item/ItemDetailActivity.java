package com.ff.modealapplication.app.ui.item;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.util.GPSPreference;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.market.MarketDetailInformationActivity;
import com.ff.modealapplication.app.ui.message.MessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ItemService itemService = new ItemService();
    private BookmarkService bookmarkService = new BookmarkService();

    private Button bookmark_button; // 즐겨찾기
    private boolean isChecked; // 상품 보이기 / 숨기기
    private Long shopNo; // 해당 상품 매장에만 메뉴 띄우기 위해서...
    //    private ViewFlipper flipper; // 뷰플리퍼
    private List<Map<String, Object>> itemList; // 동일매장 상품리스트

    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_item_detail); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);                // 뒤로가기 화살표 표시

        new ItemDetailTask().execute();

        Toast.makeText(this, "로그인정보(자신의 매장정보) : " + LoginPreference.getValue(getApplicationContext(), "shopNo") + " 상점NO : " + getIntent().getLongExtra("shopNo", -1) , Toast.LENGTH_SHORT).show();        // 자기 매장의 no값 출력(일반사용자는 -1;매장이없기때문)
//        Toast.makeText(this, "상품에서받아온정보 : " + (Double.valueOf(getIntent().getStringExtra("shopNo"))).longValue(), Toast.LENGTH_SHORT).show();

        // 해당 상품 매장아이디로 접속시 삭제/수정/보이기(숨기기)버튼 보임
        if ((Long) LoginPreference.getValue(getApplicationContext(), "shopNo") == getIntent().getLongExtra("shopNo", -1)) {
            // 삭제 버튼 클릭시
            findViewById(R.id.item_detail_button_delete).setOnClickListener(this);

            // 수정 버튼 클릭시
            findViewById(R.id.item_detail_button_modify).setOnClickListener(this);

            // 보이기/숨기기 버튼 클릭시
            ((ToggleButton) findViewById(R.id.item_detail_button_hiding)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) { // 숨기기
                        new ItemView(0L).execute();
                    } else { // 보이기
                        new ItemView(1L).execute();
                        // 보이기 누르면 해당 알림 구독한 사용자들에게 알림 전송
                        new Thread() {
                            public void run() {
                                MessagingService.send(((TextView) findViewById(R.id.item_detail_name)).getText().toString() + " 상품이 등록되었습니다.", // 제목
                                        ((TextView) findViewById(R.id.item_detail_shop_name)).getText().toString() + " 매장의 " + ((TextView) findViewById(R.id.item_detail_name)).getText().toString() + " 상품이 등록되었습니다.", // 내용
                                        "bi" + getIntent().getLongExtra("no", -1)); // 알림 번호
                            }
                        }.start();
                    }
                }
            });
            findViewById(R.id.owner_menu).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.item_detail_shop_name).setOnClickListener(new View.OnClickListener() {                // 매장이름 클릭 시 매장상세페이지로 이동
            @Override
            public void onClick(View v) {                                       // 매장상세정보로 페이지 이동
                Intent intent = new Intent(ItemDetailActivity.this, MarketDetailInformationActivity.class);

                intent.putExtra("ShopNo", getIntent().getLongExtra("shopNo", -1));              // MarketDetailInformationActivity 클래스로 ShopNo 값을 넘김
                Toast.makeText(getApplicationContext(), getIntent().getLongExtra("shopNo", -1) + "", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            }
        });

        // 즐겨찾기
        bookmark_button = (Button) findViewById(R.id.bookmark_button);

        if ((Long) LoginPreference.getValue(getApplicationContext(), "no") != -1) { // 로그인시 서버에서 즐겨찾기를 가져옴
            new BookmarkSelect().execute();
        } else { // 비로그인시
            isChecked = false;
        }
        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Long) LoginPreference.getValue(getApplicationContext(), "no") != -1) { // 로그인시
                    if (isChecked == false) { // 즐겨찾기 추가
                        Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        Log.d("상품 topic 알림 등록", "bi" + getIntent().getLongExtra("no", -1));
                        FirebaseMessaging.getInstance().subscribeToTopic("bi" + getIntent().getLongExtra("no", -1)); // 즐겨찾기 상품 알림 설정
                        bookmark_button.setBackground(getResources().getDrawable(R.drawable.heart_full)); // 즐겨찾기 추가 이미지 변경
                        new BookmarkAdd().execute(); // 서버에 즐겨찾기 정보 저장
                        isChecked = true;
                    } else { // 즐겨찾기 해제
                        Toast.makeText(getApplicationContext(), "즐겨찾기가 해제되었습니다", Toast.LENGTH_SHORT).show();
                        Log.d("상품 topic 알림 해제", "bi" + getIntent().getLongExtra("no", -1));
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("bi" + getIntent().getLongExtra("no", -1)); // 즐겨찾기 상품 알림 해제
                        bookmark_button.setBackground(getResources().getDrawable(R.drawable.heart_empty)); // 즐겨찾기 해제 이미지로 변경
                        new BookmarkDelete().execute(); // 서버에서 즐겨찾기 정보 삭제
                        isChecked = false;
                    }
                } else { // 비로그인시
                    Toast.makeText(getApplicationContext(), "즐겨찾기 서비스를 이용하려면 로그인하세요", Toast.LENGTH_SHORT).show();
                    bookmark_button.setBackground(getResources().getDrawable(R.drawable.heart_empty));
                }
            }
        });

        // 뷰페이저
        new ItemList().execute();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageAdapter adapter = new ImageAdapter(getApplicationContext());
                viewPager.setAdapter(adapter);
                viewPager.setPageMargin(getResources().getDisplayMetrics().widthPixels / -4);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setCurrentItem(1);
            }
        }, 2000);
        findViewById(R.id.image_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur = viewPager.getCurrentItem();    //현재 아이템 포지션
                if (cur > 0)                //첫 페이지가 아니면
                    viewPager.setCurrentItem(cur - 1, true);    //이전 페이지로 이동
                else                        //첫 페이지 이면
                    Toast.makeText(getApplicationContext(), "맨 처음 페이지 입니다.",
                            Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.image_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cur = viewPager.getCurrentItem();    //현재 아이템 포지션
                if (cur < itemList.size() - 1)        //마지막 페이지가 아니면
                    viewPager.setCurrentItem(cur + 1, true);    //다음 페이지로 이동
                else                        //마지막 페이지 이면
                    Toast.makeText(getApplicationContext(), "맨 마지막 페이지 입니다.",
                            Toast.LENGTH_SHORT).show();    //메시지 출력
            }
        });

//        // 뷰플리퍼 (자동 슬라이드 되는 동일 매장 상품 이미지)
//        flipper = (ViewFlipper) findViewById(R.id.item_detail_flipper);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < itemList.size(); i++) {
//                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
//                    linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//                    ImageView img = new ImageView(getApplicationContext());
//                    img.setScaleType(ImageView.ScaleType.CENTER);
//                    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
//                    ImageLoader.getInstance().displayImage(Base.url + "modeal/shop/images/" + itemList.get(i).get("picture"), img, displayImageOption);
//
//                    linearLayout.addView(img);
//                    flipper.addView(linearLayout);
//                    flipper.setDisplayedChild(i);
//                    flipper.setOnClickListener(new View.OnClickListener() { // 클릭시 해당 상품 상세페이지로 이동
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
//                            intent.putExtra("no", ((Double) itemList.get(flipper.getDisplayedChild()).get("no")).longValue());
//                            intent.putExtra("shopNo", ((Double) itemList.get(flipper.getDisplayedChild()).get("shopNo")).longValue());
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
//                }
//                Animation shownIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
//                flipper.setInAnimation(shownIn);
//                flipper.setOutAnimation(getApplicationContext(), android.R.anim.fade_out);
//                flipper.setFlipInterval(2000);
//                flipper.startFlipping();
//            }
//        }, 1000);
//
//        findViewById(R.id.image_left).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flipper.showNext();
//            }
//        });
//        findViewById(R.id.image_right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flipper.showPrevious();
//            }
//        });
    }

    // 뷰페이저를 쓰기 위한 어댑터
    public class ImageAdapter extends PagerAdapter {
        Context context;

        ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            if (itemList == null) {
                return 0;
            }
            return itemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            ImageLoader.getInstance().displayImage(itemList.get(position).get("picture").toString(), imageView, displayImageOption);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                    intent.putExtra("no", ((Double) itemList.get(position).get("no")).longValue());
                    intent.putExtra("shopNo", ((Double) itemList.get(position).get("shopNo")).longValue());
                    startActivity(intent);
                    finish();
                }
            });

            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    // 뒤로가기 클릭시 & 돋보기 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 뒤로가지 클릭시
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_detail_button_delete: {
                new AlertDialog.Builder(this).
                        setTitle("삭제").                                                          // 다이얼로그 타이틀명
                        setIcon(R.drawable.delete).                                                // 다이얼로그 이미지위치.이미지명
                        setMessage("해당 상품을\n삭제하시겠습니까?\n").                         // 다이얼로그 글
                        setPositiveButton("예", new DialogInterface.OnClickListener() {            // 긍정버튼

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ItemDelete(getIntent().getLongExtra("no", -1)).execute();
                        Log.d("setPositiveButton", "" + which);
                    }
                }).
                        setNegativeButton("아니요", new DialogInterface.OnClickListener() {        // 부정버튼

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).
                        show();
                break;
            }

            case R.id.item_detail_button_modify: {
                Intent intent = new Intent(ItemDetailActivity.this, ItemModifyActivity.class);
                intent.putExtra("no", getIntent().getLongExtra("no", -1));
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    // 상품 상세 목록
    private class ItemDetailTask extends SafeAsyncTask<Map<String, Object>> {
        @Override
        public Map<String, Object> call() throws Exception {
            // getIntent().getStringExtra("no") ← 이전 액티비티에서 no값 받아옴 (이걸로 서버에 접근해서 해당 정보 가져오기 위해서...)
            return itemService.itemDetail((String) GPSPreference.getValue(getApplicationContext(), "latitude"),
                    (String) GPSPreference.getValue(getApplicationContext(), "longitude"), getIntent().getLongExtra("no", -1L));
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.e("Error : ", e + "!!!");
            super.onException(e);
        }

        @Override // 성공하면 해당 매장명과 상품목록 출력
        protected void onSuccess(Map<String, Object> itemMap) throws Exception {
//            StringTokenizer tokens = new StringTokenizer(itemMap.get("expDate").toString(), "-/: ");
//            String year = tokens.nextToken();
//            String month = tokens.nextToken();
//            String day = tokens.nextToken();
//            String hour = tokens.nextToken();
//            String minute = tokens.nextToken();

            final TextView clock = ((TextView) findViewById(R.id.item_detail_clock));
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = format.parse(itemMap.get("expDate").toString());
            Date currentDate = new Date();
            long diff = date.getTime() -  currentDate.getTime();
            CountDownTimer timer = new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    int days = (int) (millisUntilFinished / (1000 * 60 * 60 * 24));
                    String newtime = days + ":" + hours + ":" + minutes + ":" + seconds;

                    if (newtime.equals("0:0:0:0")) {
                        clock.setText("판매종료");
                    } else if (days == 0 && hours != 0) {
                        clock.setText(hours + "시간  " + minutes + "분  " + seconds + "초 남음");
                    } else if (days == 0 && hours == 0) {
                        clock.setText(minutes + "분  " + seconds + "초 남음");
                    } else {
                        clock.setText(days + "일  " + hours + "시간  " + minutes + "분  " + seconds + "초 남음");
                    }
                }

                @Override
                public void onFinish() {
                    clock.setText("판매종료");
                }
            }.start();
            ((TextView) findViewById(R.id.item_detail_name)).setText(itemMap.get("name").toString());
            ((TextView) findViewById(R.id.item_detail_ori_price)).setText(((Double) itemMap.get("oriPrice")).longValue() + "");
            ((TextView) findViewById(R.id.item_detail_price)).setText(((Double) itemMap.get("price")).longValue() + "");
            ((TextView) findViewById(R.id.item_detail_shop_name)).setText(itemMap.get("shopName").toString());
            if (itemMap.get("grade") != null) {
                ((RatingBar) findViewById(R.id.item_detail_ratingBar)).setRating(((Double) itemMap.get("grade")).floatValue());
            }
            if (itemMap.get("distance") != null) {
                ((TextView) findViewById(R.id.item_detail_distance)).setText(((Double) itemMap.get("distance")).longValue() + "m");
            }
            if (((Double) itemMap.get("showItem")).longValue() == 0L) {
                ((ToggleButton) findViewById(R.id.item_detail_button_hiding)).setChecked(true);
            }

            ImageLoader.getInstance().displayImage(itemMap.get("picture").toString(), (ImageView) findViewById(R.id.item_detail_image), displayImageOption); // 상품이미지
        }
    }

    // 즐겨찾기 추가
    private class BookmarkAdd extends SafeAsyncTask<Void> {

        @Override
        public Void call() throws Exception {
            bookmarkService.bookmarkAdd(getIntent().getLongExtra("no", -1), (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("shopNo", -1));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 즐겨찾기 삭제
    private class BookmarkDelete extends SafeAsyncTask<Void> {

        @Override
        public Void call() throws Exception {
            bookmarkService.bookmarkDelete(getIntent().getLongExtra("no", -1), (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("shopNo", -1));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 즐겨찾기 검색
    private class BookmarkSelect extends SafeAsyncTask<Long> {

        @Override
        public Long call() throws Exception {
            return bookmarkService.bookmarkSelect(getIntent().getLongExtra("no", -1), (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("shopNo", -1));
        }

        @Override
        protected void onSuccess(Long no) throws Exception {
            super.onSuccess(no);
            if (no != null) {
                bookmark_button.setBackground(getResources().getDrawable(R.drawable.heart_full));
                isChecked = true;
            } else {
                bookmark_button.setBackground(getResources().getDrawable(R.drawable.heart_empty));
                isChecked = false;
            }
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 상품 보이기 / 숨기기
    private class ItemView extends SafeAsyncTask<Void> {
        private Long check;

        public ItemView(Long check) {
            this.check = check;
        }

        @Override
        public Void call() throws Exception {
            itemService.itemView(getIntent().getLongExtra("no", -1), check);
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*View Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 동일 매장 상품 가져오기
    private class ItemList extends SafeAsyncTask<List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> call() throws Exception {
            List<Map<String, Object>> list = itemService.itemList(getIntent().getLongExtra("shopNo", -1));
            List<Map<String, Object>> list_show = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (((Double) list.get(i).get("showItem")).longValue() == 1) {
                    list_show.add(list.get(i));
                }
            }
            return list_show;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            itemList = list;
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }
    private class ItemDelete extends SafeAsyncTask<Void> {
        Long no;

        public ItemDelete(Long no) {
            this.no = no;
        }

        @Override
        public Void call() throws Exception {
            itemService.itemDelete(no);
            return null;
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.d("!!!!!!!!!!!!!", "" + e);
            super.onException(e);
        }

        @Override // 성공하면 해당 상품이 삭제된 상품목록 출력
        protected void onSuccess(Void Void) throws Exception {

            Toast.makeText(getApplicationContext(), "해당 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
