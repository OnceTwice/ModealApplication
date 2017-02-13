package com.ff.modealapplication.app.ui.modify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.UserModifyService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.vo.UserVo;

import java.util.List;

public class UserModifyActivity extends AppCompatActivity {
    private UserModifyService userModifyService = new UserModifyService();

    private TextView tvID;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private RadioGroup group;
    private RadioButton man;
    private RadioButton woman;

    private EditText etCity;
    private EditText etGu;
    private EditText etDong;

    private EditText etYear;
    private Spinner spinnerMonth;
    private EditText etDay;

    private Button btnModifyCancel;
    private Button btnModifySubmit;

    Long no;
    String id = "";
    String password = "";
    String gender = "";

    String city = "";
    String gu = "";
    String dong = "";

    String year = "";
    String month = "";
    String day = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify);

        tvID = (TextView) findViewById(R.id.etUserModifyId);
        etPassword = (EditText) findViewById(R.id.etUserModifyPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etUserModifyPasswordConfirm);

        group = (RadioGroup) findViewById(R.id.radioGroupUserModifyGender);
        man = (RadioButton) findViewById(R.id.UserModifyRadio_man);
        woman = (RadioButton) findViewById(R.id.UserModifyRadio_woman);

        etCity = (EditText) findViewById(R.id.etUserModifyCity);
        etGu = (EditText) findViewById(R.id.etUserModifyGu);
        etDong = (EditText) findViewById(R.id.etUserModifyDong);

        etYear = (EditText) findViewById(R.id.etUserModifyYear);
        spinnerMonth = (Spinner) findViewById(R.id.spUserModifyMonth);
        etDay = (EditText) findViewById(R.id.etUserModifyDay);

        btnModifyCancel = (Button) findViewById(R.id.btnUserModifyCancel);
        btnModifySubmit = (Button) findViewById(R.id.btnUserModifySubmit);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user_modify); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);              // 뒤로가기 화살표 표시

        no = (Long)LoginPreference.getValue(getApplicationContext(), "no");
        id = (String) LoginPreference.getValue(getApplicationContext(), "id");
        tvID.setText(id);     // 사용자 ID는 수정에서 제외

        /********  성별입력(최초선택)    ********/
        if(man.isChecked()) {
            gender = "man";
            Log.d("사용자 젠더젠더12121212", gender);
        }
        else if(woman.isChecked()) {
            gender = "woman";
            Log.d("사용자 젠더젠더13131313", gender);
        }

        /********  성별입력(변경)    ********/
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.UserModifyRadio_man : {
                        gender = "man";
                        Log.d("사용자 젠더젠더", gender);
                        break;
                    }
                    case R.id.UserModifyRadio_woman : {
                        gender = "woman";
                        Log.d("사용자 젠더젠더", gender);
                        break;
                    }
                }
            }
        });

        /********   생년월일입력    ********/
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                month = (String)adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("------>", "onNothingMonthSelected");
            }
        });

        btnModifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnModifySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 입력 확인
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    etPassword.setError("비밀번호를 입력하세요");
                    etPassword.requestFocus();
                    return;
                } else if (!isPasswordValid(etPassword.getText().toString())) {
                    etPassword.setError("비밀번호가 너무 짧아요");
                    etPassword.requestFocus();
                    return;
                }

                // 비밀번호 확인 입력 확인
                if (TextUtils.isEmpty(etPasswordConfirm.getText().toString())) {
                    etPasswordConfirm.setError("비밀번호를 입력하세요");
                    etPasswordConfirm.requestFocus();
                    return;
                }

                // 비밀번호 일치 확인
                if( !etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()) ) {
                    Toast.makeText(UserModifyActivity.this, "사용자 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }

                new UserModifyAsyncTask().execute();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    // 뒤로가기 클릭시 & 돋보기 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 뒤로가지 클릭시
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        // 돋보기 클릭시
        else if (item.getItemId() == R.id.action_button) {
            // 돋보기 클릭시 수행할 작업
        }
        return true;
    }

    private class UserModifyAsyncTask extends SafeAsyncTask<List<UserVo>> {
        @Override
        public List<UserVo> call() throws Exception {
            password = etPassword.getText().toString();

            city = etCity.getText().toString()+"시 ";
            gu = etGu.getText().toString()+"구 ";
            dong = etDong.getText().toString()+"동 ";

            year = etYear.getText().toString();
            day = etDay.getText().toString();

            Log.d("번호", ""+no);
            Log.d("패스워드", ""+password);
            Log.d("성별", ""+gender);
            Log.d("지역", ""+city+gu+dong);
            Log.d("생년월일", ""+year+month+day);

            List<UserVo> list = userModifyService.fetchUserModify(no, password, gender, city+gu+dong, year+month+day);

            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("사용자", "회원정보수정 에러뜸!!!"+e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<UserVo> userVos) throws Exception {
//            super.onSuccess(userVos);
            finish();
        }
    }
}
