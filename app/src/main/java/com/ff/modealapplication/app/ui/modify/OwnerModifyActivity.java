package com.ff.modealapplication.app.ui.modify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.OwnerModifyService;
import com.ff.modealapplication.app.core.service.map.JoinMapInfoVo;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.vo.ShopVo;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.ff.modealapplication.app.ui.map.SearchShopToJoinActivity;
import com.ff.modealapplication.app.ui.map.SearchToShopInMapActivity;

public class OwnerModifyActivity extends AppCompatActivity {
    private OwnerModifyService ownerModifyService = new OwnerModifyService();

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

    private EditText etMarketName;
    private Button btnMarketSearch;

    private EditText etMarketAddress;

    private EditText etMarketPhoneNumber;

    private ImageView imageView;

    private EditText etMarketIntroduce;

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

    Long managerIdentified;
    Long shopNo;

    String marketName = "";
    String marketAddress = "";
    String marketNewAddress = "";
    String marketPhoneNumber = "";
    String imageURL = "";
    String marketIntroduce = "";
    String longitude = "";
    String latitude = "";

    final int[] flag = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_modify);

        tvID = (TextView) findViewById(R.id.etOwnerModifyId);
        etPassword = (EditText) findViewById(R.id.etOwnerModifyPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etOwnerModifyPasswordConfirm);

        group = (RadioGroup) findViewById(R.id.radioGroupOwnerModifyGender);
        man = (RadioButton) findViewById(R.id.OwnerModifyRadio_man);
        woman = (RadioButton) findViewById(R.id.OwnerModifyRadio_woman);

        etCity = (EditText) findViewById(R.id.etOwnerModifyCity);
        etGu = (EditText) findViewById(R.id.etOwnerModifyGu);
        etDong = (EditText) findViewById(R.id.etOwnerModifyDong);

        etYear = (EditText) findViewById(R.id.etOwnerModifyYear);
        spinnerMonth = (Spinner) findViewById(R.id.spOwnerModifyMonth);
        etDay = (EditText) findViewById(R.id.etOwnerModifyDay);

        etMarketName = (EditText) findViewById(R.id.etOwnerModifyMarketName);
        btnMarketSearch = (Button) findViewById(R.id.btnOwnerModifyAddressSearch);

        etMarketAddress = (EditText) findViewById(R.id.etOwnerModifyMarketAddress);
        etMarketPhoneNumber = (EditText) findViewById(R.id.etOwnerModifyMarketPhoneNumber);

        imageView = (ImageView) findViewById(R.id.imgViewOwnerModifyMarketImage);

        btnModifyCancel = (Button) findViewById(R.id.btnOwnerModifyCancel);
        btnModifySubmit = (Button) findViewById(R.id.btnOwnerModifySubmit);

        etMarketIntroduce = (EditText) findViewById(R.id.etOwnerModifyMarketIntroduce);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_owner_modify); // 아이디인 툴바 로그인 부분은 내 아이디로 변경해줘야함
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        no = (Long) LoginPreference.getValue(getApplicationContext(), "no");
        id = (String) LoginPreference.getValue(getApplicationContext(), "id");
        tvID.setText(id);     // 사용자 ID는 수정에서 제외

        managerIdentified = (Long) LoginPreference.getValue(getApplicationContext(), "managerIdentified");
        shopNo = (Long) LoginPreference.getValue(getApplicationContext(), "shopNo");

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
                    case R.id.OwnerModifyRadio_man : {
                        gender = "man";
                        Log.d("사용자 젠더젠더", gender);
                        break;
                    }
                    case R.id.OwnerModifyRadio_woman : {
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

        /******************************************* 사용자 & 사업자 경계 **************************************************/

        btnMarketSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String items[] = {"매장명으로 검색", "지도에서 직접 찾기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(OwnerModifyActivity.this);
                builder.setTitle("Title");
                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getActivity(), i+"", Toast.LENGTH_SHORT).show();
                        flag[0] = i;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(flag[0] == 0) {
                            Intent intentToMap = new Intent(OwnerModifyActivity.this, SearchShopToJoinActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        } else if(flag[0] == 1) {
                            Intent intentToMap = new Intent(OwnerModifyActivity.this, SearchToShopInMapActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Cancel 버튼 클릭 시
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        btnModifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnModifySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Toast.makeText(OwnerModifyActivity.this, "사용자 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }

                new OwnerModifyAsyncTask().execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== RESULT_OK && requestCode == 1000) {
            if(flag[0] == 0) {
                JoinMapInfoVo joinMapInfoVo = (JoinMapInfoVo) data.getSerializableExtra("joinMapInfoVo");

                etMarketName.setText(joinMapInfoVo.getTitle());         // 매장명
                etMarketAddress.setText(joinMapInfoVo.getAddress());    // 매장주소
                marketNewAddress = joinMapInfoVo.getNewAddress();
                etMarketPhoneNumber.setText(joinMapInfoVo.getPhone());  // 매장전화번호
                imageURL = joinMapInfoVo.getImageUrl();
                longitude = joinMapInfoVo.getLongitude();
                latitude = joinMapInfoVo.getLatitude();

                Log.d("onActivityResult 이미지", imageURL);

            } else if(flag[0] == 1) {
                longitude = data.getStringExtra("longitude");
                latitude = data.getStringExtra("latitude");
            }
        } else {

        }
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

    private class OwnerModifyAsyncTask extends SafeAsyncTask<UserVo> {
        private UserVo userVo = new UserVo();
        private ShopVo shopVo = new ShopVo();

        @Override
        public UserVo call() throws Exception {
            password = etPassword.getText().toString();

            city = etCity.getText().toString()+"시 ";
            gu = etGu.getText().toString()+"구 ";
            dong = etDong.getText().toString()+"동 ";

            year = etYear.getText().toString();
            day = etDay.getText().toString();

            marketName = etMarketName.getText().toString();
            marketAddress = etMarketAddress.getText().toString();
            marketPhoneNumber = etMarketPhoneNumber.getText().toString();
            marketIntroduce = etMarketIntroduce.getText().toString();

            userVo.setNo(no);
            userVo.setId(id);
            userVo.setPassword(password);
            userVo.setGender(gender);
            userVo.setLocation(city+gu+dong);
            userVo.setBirth(year+month+day);
            userVo.setManagerIdentified(managerIdentified);
            userVo.setShopNo(shopNo);

            shopVo.setNo(shopNo);
            shopVo.setAddress(marketAddress);
            shopVo.setNewAddress(marketNewAddress);
            shopVo.setName(marketName);
            shopVo.setPhone(marketPhoneNumber);
            shopVo.setPicture(imageURL);
            shopVo.setIntroduce(marketIntroduce);

            if(longitude == null) {
                shopVo.setLongitude(Double.parseDouble("0.0"));
            } else {
                shopVo.setLongitude(Double.parseDouble(longitude));
            }
            if(latitude == null) {
                shopVo.setLatitude(Double.parseDouble("0.0"));
            } else {
                shopVo.setLatitude(Double.parseDouble(latitude));
            }

            UserVo ownerVo = ownerModifyService.fetchOwnerList(userVo, shopVo);

            return ownerVo;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("사업자", "회원정보수정 에러뜸!!!"+e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
            super.onSuccess(userVo);
            finish();
        }
    }
}
