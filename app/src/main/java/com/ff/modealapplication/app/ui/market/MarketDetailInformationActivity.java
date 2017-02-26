package com.ff.modealapplication.app.ui.market;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.domain.CommentVo;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.service.CommentRegisterService;
import com.ff.modealapplication.app.core.service.CommentService;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.service.MarketService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.comment.CommentListAdapter;
import com.ff.modealapplication.app.ui.item.ItemDetailActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ff.modealapplication.R.id.rbOutput;

public class MarketDetailInformationActivity extends AppCompatActivity {
    private MarketService marketService = new MarketService();
    private CommentService commentService = new CommentService();
    private CommentRegisterService commentRegisterService = new CommentRegisterService();

    // 즐겨찾기용
    private BookmarkService bookmarkService = new BookmarkService();

    // 동일매장상품(뷰페이저)
    private ItemService itemService = new ItemService();
    private List<Map<String, Object>> itemList; // 동일매장 상품리스트

    private ScrollView scrollView;

    private CommentListAdapter commentListAdapter = null;
    private ListView listView = null;

    private ImageView imageView;
    private Bitmap bitmap;
    private TextView tvName;
    private RatingBar outputRatingBar;
    private TextView tvAddress;
    private TextView tvIntroduce;
    private Button btnBookmark;

    private ListView listViewComment;

    private RatingBar inputRatingBar;
    private TextView tvComment;

    private Button btnSubmit;
    private int marketScore=0;
    private boolean isChecked; // 상품 보이기 / 숨기기

    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail_information);

        scrollView = (ScrollView) findViewById(R.id.marketScrollView);

        imageView = (ImageView) findViewById(R.id.imgVMarketDetailInformationImg);
        tvName = (TextView) findViewById(R.id.tvMarketDetailInformationName);
        outputRatingBar = (RatingBar) findViewById(rbOutput);
        tvAddress = (TextView) findViewById(R.id.tvMarketDetailInformationAddress);
        tvIntroduce = (TextView) findViewById(R.id.tvMarketDetailInformationIntroduce);
        btnBookmark = (Button) findViewById(R.id.btnBookmarkMarket);

        listViewComment = (ListView) findViewById(R.id.commentList);

        inputRatingBar = (RatingBar) findViewById(R.id.rbInput);
        tvComment = (TextView) findViewById(R.id.etMarketDetailInformationComment);

        btnSubmit = (Button) findViewById(R.id.btnMarketDetailInformationSubmit);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_marketDetail); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        Toast.makeText(getApplicationContext(), "MarketDetailInformationActivity.java 에서의 ShopNo : " + getIntent().getLongExtra("ShopNo", 0), Toast.LENGTH_SHORT).show();

        // 댓글 리스트(출력)
        commentListAdapter = new CommentListAdapter(this);
        listView = (ListView) findViewById(R.id.commentList);
        listView.setAdapter(commentListAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {            // 리스트뷰터치될때에는
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);        // 스크롤뷰 기능을 막음(충돌방지)
                return false;
            }
        });

        new MarketInformationAsyncTask().execute();
        new FetchCommentListAsyncTask().execute();

        // 즐겨찾기
        if ((Long) LoginPreference.getValue(getApplicationContext(), "no") != -1) { // 로그인시 서버에서 즐겨찾기를 가져옴
            new BookmarkSelect().execute();
        } else { // 비로그인시
            isChecked = false;
        }
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Long) LoginPreference.getValue(getApplicationContext(), "no") != -1) {
                    if(isChecked == false) {        // 즐겨찾기 추가
                        Toast.makeText(getApplicationContext(), "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        Log.d("매장 topic 알림 등록", "bs" + getIntent().getLongExtra("ShopNo", 0));
                        FirebaseMessaging.getInstance().subscribeToTopic("bs" + getIntent().getLongExtra("ShopNo", 0)); // 즐겨찾기 상품 알림 설정
                        btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.bookmark_select));
                        new BookmarkAdd().execute(); // 서버에 즐겨찾기 정보 저장
                        isChecked = true;
                    } else {                        // 즐겨찾기 해제
                        Toast.makeText(getApplicationContext(), "즐겨찾기가 해제되었습니다", Toast.LENGTH_SHORT).show();
                        Log.d("매장 topic 알림 해제", "bs" + getIntent().getLongExtra("ShopNo", 0));
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("bs" + getIntent().getLongExtra("ShopNo", 0)); // 즐겨찾기 상품 알림 해제
                        btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.bookmark_unselect));
                        new BookmarkDelete().execute(); // 서버에서 즐겨찾기 정보 삭제
                        isChecked = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "즐겨찾기 서비스를 이용하려면 로그인하세요", Toast.LENGTH_SHORT).show();
                    btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.bookmark_unselect));
                }
            }
        });

        // 뷰페이저
        new ItemList().execute();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_shop);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageAdapter adapter = new ImageAdapter(getApplicationContext());
                viewPager.setAdapter(adapter);
                viewPager.setPageMargin(getResources().getDisplayMetrics().widthPixels / -4);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setCurrentItem(1);
            }
        }, 1000);
        findViewById(R.id.image_left_shop).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.image_right_shop).setOnClickListener(new View.OnClickListener() {
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

        inputRatingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                showRatingDialog();
                standardDialog();

                return false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Long)LoginPreference.getValue(getApplicationContext(), "no") == -1) {
                    Toast.makeText(getApplicationContext(), "로그인 유저만 사용 가능합니다", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(marketScore == 0) {
                    Toast.makeText(getApplicationContext(), "평점을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(tvComment.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                new RegisterCommentAsyncTask().execute();
            }
        });
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
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

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

    public void standardDialog() {
        String score[] = {"아주좋아요", "좋아요", "보통", "싫어요", "아주싫어요"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("평점");
        builder.setSingleChoiceItems(score, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {            // 최초선택: which:-1, marketScore:0
                marketScore = Math.abs(which-5);
                Toast.makeText(MarketDetailInformationActivity.this, which + "점 선택 \n" + marketScore+"점 선택", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {        // which : 무조건 -1
                if(marketScore == 0) {
                    marketScore = 5;
                    inputRatingBar.setRating(marketScore);
                    return;
                }

                Toast.makeText(MarketDetailInformationActivity.this, which + "점 선택 \n" + marketScore+"점 선택", Toast.LENGTH_SHORT).show();
                inputRatingBar.setRating(marketScore);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showRatingDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar ratingBar = new RatingBar(this);

        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("평점");
        popDialog.setView(ratingBar);

        ratingBar.setMax(5);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize((float) 1.0);

        popDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "평점 : " + which, Toast.LENGTH_SHORT).show();
                inputRatingBar.setRating(ratingBar.getRating());
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        popDialog.create();
        popDialog.show();
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

    private class MarketInformationAsyncTask extends SafeAsyncTask<Map<String, Object>> {
        @Override
        public Map<String, Object> call() throws Exception {
            Log.d("MarketInfncTask : ", "입갤");
            return marketService.marketInform(getIntent().getLongExtra("ShopNo", 0));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("MarketInformation == ", "서비스 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(final Map<String, Object> shopInformMap) throws Exception {
            super.onSuccess(shopInformMap);
            Log.d("MarketInfncTask : ", "성공!!!");

            // 평점
            if (shopInformMap.get("grade") != null) {
                ((RatingBar) findViewById(R.id.rbOutput)).setRating(((Double) shopInformMap.get("grade")).floatValue());
            }

            /////////////////////////////// 이미지 세팅(URL로 가지고 올 때) ////////////////////////////////////
            //  안드로이드에서 네트워크 관련 작업을 할 때는
            //  반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 함
            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(shopInformMap.get("picture").toString());    // URL 주소를 이용해서 URL 객체 생성

                        //  아래 코드는 웹에서 이미지를 가져온 뒤
                        //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException ex) {

                    }
                }
            };

            mThread.start();        // 웹에서 이미지를 가져오는 작업 스레드 실행.

            try {
                //  메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
                //  대기해야 하므로 작업스레드의 join() 메소드를 호출해서
                //  메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.
                mThread.join();

                //  이제 작업 스레드에서 이미지를 불러오는 작업을 완료했기에
                //  UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정합니다.
                imageView.setImageBitmap(bitmap);
            } catch (InterruptedException e) {

            }
            ////////////////////////////////////////////////////////////////////////////////////

            tvName.setText(shopInformMap.get("name").toString());
//            tvAvgScore.setText(shopInformMap.get("").toString());
            tvAddress.setText(shopInformMap.get("address").toString());
            tvIntroduce.setText(shopInformMap.get("introduce").toString());

        }
    }

    private class FetchCommentListAsyncTask extends SafeAsyncTask<List<CommentVo>> {
        @Override
        public List<CommentVo> call() throws Exception {
            return commentService.commentInform(getIntent().getLongExtra("ShopNo", 0));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("매장상세페이지", "서비스 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<CommentVo> commentVos) throws Exception {
            commentListAdapter.add(commentVos);
            super.onSuccess(commentVos);
            Log.d("FetchCommentListAsy : ", "성공!!!");
        }
    }

    private class RegisterCommentAsyncTask extends SafeAsyncTask<List<CommentVo>> {
        private CommentVo commentVo = new CommentVo();

        @Override
        public List<CommentVo> call() throws Exception {
            Log.d("RegisterCommentAsy", "리스트 등록 어싱크!!");

            commentVo.setContent(tvComment.getText().toString());
            commentVo.setGrade((long) marketScore);
            commentVo.setShopNo(getIntent().getLongExtra("ShopNo", 0));
            commentVo.setUserNo((Long) LoginPreference.getValue(getApplicationContext(), "no"));

            return commentRegisterService.commentRegister(commentVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<CommentVo> commentVos) throws Exception {
            tvComment.setText("");
            inputRatingBar.setRating(0);
            commentListAdapter.clear();
            commentListAdapter.add(commentVos);

            Toast.makeText(getApplicationContext(), "등록이 완료되었습니다", Toast.LENGTH_SHORT).show();
            super.onSuccess(commentVos);
        }
    }

    // 즐겨찾기 검색
    private class BookmarkSelect extends SafeAsyncTask<Long> {

        @Override
        public Long call() throws Exception {
            return bookmarkService.bookmarkSelect(null, (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("ShopNo", 0));
        }

        @Override
        protected void onSuccess(Long no) throws Exception {
            super.onSuccess(no);
            if (no != null) {
                btnBookmark.setBackground(getResources().getDrawable(R.drawable.bookmark_select));
                isChecked = true;
            } else {
                btnBookmark.setBackground(getResources().getDrawable(R.drawable.bookmark_unselect));
                isChecked = false;
            }
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 즐겨찾기 추가
    private class BookmarkAdd extends SafeAsyncTask<Void> {

        @Override
        public Void call() throws Exception {
            bookmarkService.bookmarkAdd(null, (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("ShopNo", 0));
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
            bookmarkService.bookmarkDelete(null, (Long) LoginPreference.getValue(getApplicationContext(), "no"), getIntent().getLongExtra("ShopNo", 0));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 동일 매장 상품 가져오기
    private class ItemList extends SafeAsyncTask<List<Map<String, Object>>> {

        @Override
        public List<Map<String, Object>> call() throws Exception {
            List<Map<String, Object>> list = itemService.itemList(getIntent().getLongExtra("ShopNo", 0));
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
}
