package com.ff.modealapplication.app.ui.join;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.UserJoinService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.domain.UserVo;

/**
 * Created by BIT on 2017-02-14.
 */

public class JoinLeaveActivity extends AppCompatActivity {

    private UserJoinService userJoinService = new UserJoinService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_leave);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_join_leave);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        findViewById(R.id.leave_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.leave_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LeaveAsyncTask().execute();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) { // 뒤로가기 클릭시 searchActivity 종료
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class LeaveAsyncTask extends SafeAsyncTask<UserVo>{

        @Override
        public UserVo call() throws Exception {
            userJoinService.UserLeave();
            LoginPreference.removeAll(getApplicationContext());
            finish();
            return null;
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
//            super.onSuccess(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException("LeaveException : "+e);
        }
    }
}
