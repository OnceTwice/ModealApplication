package com.ff.modealapplication.app.ui.market;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.domain.CommentVo;
import com.ff.modealapplication.app.core.service.CommentRegisterService;
import com.ff.modealapplication.app.core.service.CommentService;
import com.ff.modealapplication.app.core.service.MarketService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.comment.CommentListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MarketDetailInformationActivity extends AppCompatActivity {
    private MarketService marketService = new MarketService();
    private CommentService commentService = new CommentService();
    private CommentRegisterService commentRegisterService = new CommentRegisterService();

    private CommentListAdapter commentListAdapter = null;
    private ListView listView = null;

    private ImageView imageView;
    private Bitmap bitmap;
    private TextView tvName;
    private RatingBar outputRatingBar;
    private TextView tvAddress;
    private TextView tvIntroduce;
    private Button btnBookmark;

    private RatingBar inputRatingBar;
    private TextView tvComment;

    private Button btnSubmit;
    private int marketScore=0;
    private boolean isChecked = false; // 상품 보이기 / 숨기기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail_information);

        imageView = (ImageView) findViewById(R.id.imgVMarketDetailInformationImg);
        tvName = (TextView) findViewById(R.id.tvMarketDetailInformationName);
        outputRatingBar = (RatingBar) findViewById(R.id.rbOutput);
        tvAddress = (TextView) findViewById(R.id.tvMarketDetailInformationAddress);
        tvIntroduce = (TextView) findViewById(R.id.tvMarketDetailInformationIntroduce);
        btnBookmark = (Button) findViewById(R.id.btnBookmarkMarket);

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

        new MarketInformationAsyncTask().execute();
        new FetchCommentListAsyncTask().execute();

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Long) LoginPreference.getValue(getApplicationContext(), "no") != -1) {
                    if(isChecked == false) {        // 즐겨찾기 추가
                        Toast.makeText(getApplicationContext(), "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.heart_full));
                        isChecked = true;
                    } else {                        // 즐겨찾기 해제
                        Toast.makeText(getApplicationContext(), "북마크에 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.heart_empty));
                        isChecked = false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "즐겨찾기 서비스를 이용하려면 로그인하세요", Toast.LENGTH_SHORT).show();
                    btnBookmark.setBackground(ContextCompat.getDrawable(MarketDetailInformationActivity.this, R.drawable.heart_empty));
                }
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


}
