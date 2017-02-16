package com.ff.modealapplication.app.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.LoginService;
import com.ff.modealapplication.app.core.domain.UserVo;

public class FindPasswordActivity extends AppCompatActivity {

    private View mProgressView;
    private View mLoginFormView;
    private EditText emailText;

    // 안드로이드 키보드 숨길때
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_findpw); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        emailText = (EditText) findViewById(R.id.find_password_email);

        findViewById(R.id.find_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
                attemptFind();
            }
        });

        // 안드로이드 키보드 숨길때
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mLoginFormView = findViewById(R.id.findpw_form);
        mProgressView = findViewById(R.id.findpw_progress);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void attemptFind() {
        // Reset errors.
        emailText.setError(null);

        // Store values at the time of the login attempt.
        String email = emailText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            emailText.setError("이메일을 입력하세요");
            focusView = emailText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailText.setError("이메일 형식이 아닙니다");
            focusView = emailText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            new PasswordFindAsyncTask(email).execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public class PasswordFindAsyncTask extends SafeAsyncTask<UserVo> {

        private String email;

        public PasswordFindAsyncTask(String email) {
            this.email = email;
        }

        @Override
        public UserVo call() throws Exception {
            return new LoginService().findPW(email);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
            showProgress(false);
            if (userVo == null) {
                Toast.makeText(getApplicationContext(), "가입되지 않은 이메일입니다", Toast.LENGTH_SHORT).show();
            } else {
                String email = userVo.getId()/*.substring(1, userVo.getId().lastIndexOf("\""))*/;
                Toast.makeText(getApplicationContext(), email + "로 임시비밀번호 발송", Toast.LENGTH_SHORT).show();
                finish();
            }
            // 왠지 모르지만 onSuccess가 되기전에 다른 코드를 써줘야 잘돌아감...
            super.onSuccess(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            showProgress(false);
            super.onException(e);
        }
    }
}
