package com.ff.modealapplication.app.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.service.LoginService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.ff.modealapplication.app.ui.join.JoinActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // 안드로이드 키보드 숨길때
    private InputMethodManager imm;

    // 페이스북 사용되는 콜백매니저
    private CallbackManager callbackManager;

    // 구글
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create(); // 페이스북 콜백매니저

        // 액션바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        // 회원가입
        findViewById(R.id.login_join_button).setOnClickListener(this);

        // 비밀번호 찾기
        findViewById(R.id.login_findpw_button).setOnClickListener(this);

        // 안드로이드 키보드 숨길때
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // ----- 구글 로그인 -----
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.w("구글 로그인 실패", connectionResult.getErrorMessage());
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        // ----- 구글 로그인 -----

        // 페이스북 로그인
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("public_profile", "user_birthday", "email", "user_location", "user_hometown");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        UserVo userVo = new UserVo();
                        userVo.setId(!object.optString("email").isEmpty() ? object.optString("email") : object.optString("id"));
                        userVo.setGender(!object.optString("gender").isEmpty() ? object.optString("gender") : null);
                        userVo.setLocation(!object.optString("location").isEmpty() ? object.optJSONObject("location").optString("name").substring(0, object.optJSONObject("location").optString("name").lastIndexOf(',')) : null);
                        userVo.setBirth(!object.optString("birthday").isEmpty() ? object.optString("birthday") : null);
                        userVo.setManagerIdentified(3);

                        new UserLoginTask(userVo).execute(); // 쓰레드를 써야하므로...
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, age_range, gender, locale, birthday, email, location, hometown");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    // 구글 로그인
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // 구글 로그인 성공 후
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            UserVo userVo = new UserVo();
            userVo.setId(acct.getEmail());
            userVo.setManagerIdentified(4);

            new UserLoginTask(userVo).execute();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 페이스북
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // 구글 로그인
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // 페이스북&구글 로그인 여기까지~

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button: // 구글 로그인
                signIn();
                break;
            case R.id.login_join_button: { // 회원가입
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.login_findpw_button: { // 비밀번호 찾기
                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.email_sign_in_button: { // 일반 로그인
                imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                attemptLogin();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    // 로그인 구현 하는곳
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("비밀번호를 입력하세요");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("비밀번호가 너무 짧아요");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("이메일을 입력하세요");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("이메일 형식이 아닙니다");
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private String mEmail = null;
        private String mPassword = null;
        private UserVo userVo = new UserVo();

        public UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            userVo.setId(email);
            userVo.setPassword(password);
        }

        public UserLoginTask(UserVo userVo) {
            this.userVo = userVo;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                if (userVo.getManagerIdentified() == 3) { // 페이스북 로그인시
                    UserVo serverUserVo = new LoginService().login(userVo);
                    if (serverUserVo != null) {
                        LoginPreference.put(getApplicationContext(), serverUserVo);
                    } else {
                        new LoginService().SocialJoin(userVo);
                        LoginPreference.put(getApplicationContext(), userVo);
                    }
                    return 4;
                } else if (userVo.getManagerIdentified() == 4) { // 구글 로그인시
                    UserVo serverUserVo = new LoginService().login(userVo);
                    if (serverUserVo != null) {
                        LoginPreference.put(getApplicationContext(), serverUserVo);
                    } else {
                        new LoginService().SocialJoin(userVo);
                        LoginPreference.put(getApplicationContext(), userVo);
                    }
                    return 5;
                } else {
                    userVo = new LoginService().login(userVo);
                    if (userVo != null) {
                        LoginPreference.put(getApplicationContext(), userVo);
                    }
                }
            } catch (Exception e) {
                return 0;
            }
            if (userVo == null) {
                return 1;
            } else if (!userVo.getPassword().equals(mPassword)) {
                return 2;
            }
            return 3;
        }

        @Override
        protected void onPostExecute(Integer success) {
            mAuthTask = null;
            showProgress(false);
            switch (success) {
                case 0:
                    Toast.makeText(LoginActivity.this, "잠시후 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mEmailView.setError("등록되지 않은 이메일입니다");
                    mEmailView.requestFocus();
                    break;
                case 2:
                    mPasswordView.setError("비밀번호가 다릅니다");
                    mPasswordView.requestFocus();
                    break;
                case 3:
                    finish();
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    finish();
                    Toast.makeText(getApplication(), "페이스북 로그인 성공", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    finish();
                    Toast.makeText(getApplication(), "구글 로그인 성공", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}