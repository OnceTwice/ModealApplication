package com.ff.modealapplication.app.ui.notice;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.NoticeService;
import com.ff.modealapplication.app.core.domain.NoticeVo;

import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    NoticeListArrayAdapter noticeListArrayAdapter = null;

    NoticeService noticeService = new NoticeService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notice); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시



        noticeListArrayAdapter = new NoticeListArrayAdapter(this);
        ListView listView = (ListView)findViewById(R.id.list_notice);
        listView.setAdapter(noticeListArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.notice_linear_hide) ;
                Drawable drawable = getResources().getDrawable(R.drawable.notice_next_down);
                Drawable drawable2 = getResources().getDrawable(R.drawable.notice_next);
                    if (linearLayout.getVisibility() == View.GONE) {
                        ((ImageView)view.findViewById(R.id.notice_next)).setImageDrawable(drawable);
                        linearLayout.setVisibility(View.VISIBLE);
                    } else {
                        ((ImageView)view.findViewById(R.id.notice_next)).setImageDrawable(drawable2);
                        linearLayout.setVisibility(View.GONE);

                    }


            }
        });
        new NoticeAsyncTask().execute();
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

    public class NoticeAsyncTask extends SafeAsyncTask<List<NoticeVo>>{

        @Override
        public List<NoticeVo> call() throws Exception {

            List<NoticeVo> list= noticeService.getNoticeList();

            return list;
        }

        @Override
        protected void onSuccess(List<NoticeVo> noticeVos) throws Exception {
//            super.onSuccess(noticeVos);
            noticeListArrayAdapter.add(noticeVos);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException("Notice -> "+e);
        }
    }
}
