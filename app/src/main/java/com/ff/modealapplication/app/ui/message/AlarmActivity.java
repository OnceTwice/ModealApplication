package com.ff.modealapplication.app.ui.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by BIT on 2017-02-16.
 */

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox checkOn;
    private CheckBox checkOff;
    private List<Map<String, Object>> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmList = new ArrayList();
        new AlarmAsyncTask().execute();

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_alarm); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        checkOn = (CheckBox) findViewById(R.id.alarm_on);
        checkOff = (CheckBox) findViewById(R.id.alarm_off);

        checkOn.setOnClickListener(this);
        checkOff.setOnClickListener(this);

        // 테스트 알림 보내기용 (admin으로는 보내기 가능)
        if ((Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == 0L) {
            findViewById(R.id.admin_alarm).setVisibility(View.VISIBLE);
            findViewById(R.id.push_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread() {
                        public void run() {
                            MessagingService.send(((EditText) findViewById(R.id.push_title)).getText().toString(), ((EditText) findViewById(R.id.push_body)).getText().toString(), ((EditText) findViewById(R.id.push_topic)).getText().toString());
                        }
                    }.start();
                }
            });
        } else {
            findViewById(R.id.admin_alarm).setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_on: {
                for (int i = 0; i < alarmList.size(); i++) {
                    if (alarmList.get(i).get("itemNo") != null) {
                        Log.d("상품 topic 알림 등록", "bi" + ((Double)alarmList.get(i).get("itemNo")).longValue());
                        FirebaseMessaging.getInstance().subscribeToTopic("bi" + ((Double)alarmList.get(i).get("itemNo")).longValue()); // 즐겨찾기 상품 알림 설정
                    } else if (alarmList.get(i).get("shopNo") != null) {
                        Log.d("매장 topic 알림 등록", "bs" + ((Double)alarmList.get(i).get("shopNo")).longValue());
                        FirebaseMessaging.getInstance().subscribeToTopic("bs" + ((Double)alarmList.get(i).get("shopNo")).longValue()); // 즐겨찾기 매장 알림 설정
                    }
                }
                checkOn.setChecked(true);
                checkOff.setChecked(false);
                break;
            }
            case R.id.alarm_off: {
                for (int i = 0; i < alarmList.size(); i++) {
                    if (alarmList.get(i).get("itemNo") != null) {
                        Log.d("상품 topic 알림 해제", "bi" + ((Double)alarmList.get(i).get("itemNo")).longValue());
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("bi" + ((Double)alarmList.get(i).get("itemNo")).longValue()); // 즐겨찾기 상품 알림 해제
                    } else if (alarmList.get(i).get("shopNo") != null) {
                        Log.d("매장 topic 알림 해제", "bs" + ((Double)alarmList.get(i).get("shopNo")).longValue());
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("bs" + ((Double)alarmList.get(i).get("shopNo")).longValue()); // 즐겨찾기 매장 알림 해제
                    }
                }
                checkOn.setChecked(false);
                checkOff.setChecked(true);
                break;
            }
        }
    }

    private class AlarmAsyncTask extends SafeAsyncTask<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> call() throws Exception {
            BookmarkService bookmarkService = new BookmarkService();
            List<Map<String, Object>> list = bookmarkService.bookmarkList((Long) LoginPreference.getValue(getApplicationContext(), "no"));
            return list;
        }

        @Override
        protected void onSuccess(List<Map<String, Object>> list) throws Exception {
            alarmList = list;
            super.onSuccess(list);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }
}
