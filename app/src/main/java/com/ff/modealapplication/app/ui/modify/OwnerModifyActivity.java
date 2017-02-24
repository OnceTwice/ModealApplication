package com.ff.modealapplication.app.ui.modify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.ff.modealapplication.app.core.domain.ShopVo;
import com.ff.modealapplication.app.core.domain.UserVo;
import com.ff.modealapplication.app.core.service.OwnerModifyService;
import com.ff.modealapplication.app.core.service.map.JoinMapInfoVo;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.map.SearchShopToJoinActivity;
import com.ff.modealapplication.app.ui.map.SearchToShopInMapActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private Bitmap bitmap;

    private EditText etMarketIntroduce;

    private Button btnModifyCancel;
    private Button btnModifySubmit;

    // 이미지 업로드
    private Uri uri;
    private Bitmap shop_bitmap;
    private static final int RESULT_SELECT_IMAGE = 1;

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

        imageView = (ImageView) findViewById(R.id.imgViewOwnerModifyMarketImage);          // 이미지

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
        if (man.isChecked()) {
            gender = "man";
            Log.d("사용자 젠더젠더12121212", gender);
        } else if (woman.isChecked()) {
            gender = "woman";
            Log.d("사용자 젠더젠더13131313", gender);
        }

        /********  성별입력(변경)    ********/
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.OwnerModifyRadio_man: {
                        gender = "man";
                        Log.d("사용자 젠더젠더", gender);
                        break;
                    }
                    case R.id.OwnerModifyRadio_woman: {
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
                month = (String) adapterView.getItemAtPosition(position);
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
                        if (flag[0] == 0) {
                            Intent intentToMap = new Intent(OwnerModifyActivity.this, SearchShopToJoinActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        } else if (flag[0] == 1) {
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

        // 매장 이미지 업로드
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                galleryIntent.setType("gallery*//*");

                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Image"), RESULT_SELECT_IMAGE);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image*//*");
////                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent.createChooser(intent, "Select Image"), 1);
                shop_bitmap = null;
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
                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
                    Toast.makeText(OwnerModifyActivity.this, "사용자 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etPasswordConfirm.setText("");
                    etPassword.requestFocus();
                    return;
                }
                new ImageUpload().execute(); // 이미지 업로드가 이루어 진후 성공하면 회원가입 AsyncTask가 이루어짐
//                new OwnerModifyAsyncTask().execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {
            if (flag[0] == 0) {
                JoinMapInfoVo joinMapInfoVo = (JoinMapInfoVo) data.getSerializableExtra("joinMapInfoVo");

                etMarketName.setText(joinMapInfoVo.getTitle());         // 매장명
                etMarketAddress.setText(joinMapInfoVo.getAddress());    // 매장주소
                marketNewAddress = joinMapInfoVo.getNewAddress();
                etMarketPhoneNumber.setText(joinMapInfoVo.getPhone());  // 매장전화번호
                imageURL = joinMapInfoVo.getImageUrl();
                longitude = joinMapInfoVo.getLongitude();
                latitude = joinMapInfoVo.getLatitude();

                Log.d("onActivityResult 이미지", imageURL);

                /////////////////////////////// 이미지 세팅 ////////////////////////////////////
                //  안드로이드에서 네트워크 관련 작업을 할 때는
                //  반드시 메인 스레드가 아닌 별도의 작업 스레드에서 작업해야 함
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(imageURL);    // URL 주소를 이용해서 URL 객체 생성

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

            } else if (flag[0] == 1) {
                longitude = data.getStringExtra("longitude");
                latitude = data.getStringExtra("latitude");
            }
        } else if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();

            shop_bitmap = BitmapFactory.decodeFile(getPath(uri)); // uri → bitmap 변환

            // 이미지의 가로길이 200으로 맞춰 이미지 크기 조절
            int resizeWidth = 200;
            double aspectRatio = (double) shop_bitmap.getHeight() / shop_bitmap.getWidth();
            int targetHeight = (int) (resizeWidth * aspectRatio);
            shop_bitmap = Bitmap.createScaledBitmap(shop_bitmap, resizeWidth, targetHeight, false);
            uri = getImageUri(getApplicationContext(), shop_bitmap);

            imageView.setImageURI(uri);

            Log.w("path?", uri.getPath() + " << uri.getPath(), " + uri + " << uri, " + getPath(uri) + " << getPath(uri)");
        }
    }

    // 이미지 업로드 ======================================================= 여기부터 =================================================
    // 업로드용(사진의 절대 경로 구하기)
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        // 역시 위아래 같은듯...
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
    }

    // bit -> uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // imgur에 사진 업로드
    private static final String IMGUR_CLIENT_ID = "39c074c1942156b";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private JSONObject imgur;
    private String url; // DB에 저장해야함

    private final OkHttpClient client = new OkHttpClient();

    public void run() throws Exception {
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", "logo-square.png",
                        RequestBody.create(MEDIA_TYPE_PNG, new File(getPath(uri))))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        imgur = new JSONObject(response.body().string());
        imgur = new JSONObject(String.valueOf(imgur.get("data")));
        url = imgur.getString("link");
    }

    // run()을 비동기로 돌리기 위한 AsyncTask
    public class ImageUpload extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OwnerModifyActivity.this);
            progressDialog.setMessage("상품을 등록하고 있습니다...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            ;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                run();
            } catch (Exception e) {
                Log.w("ImageUpload Error : ", e + "!!!");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.w("업로드된 주소", url + "");
            imageURL = url;
            new OwnerModifyAsyncTask().execute();
        }
    }

    // 이미지 업로드 ======================================================= 여기까지 =================================================

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

            city = etCity.getText().toString() + "시 ";
            gu = etGu.getText().toString() + "구 ";
            dong = etDong.getText().toString() + "동 ";

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
            userVo.setLocation(city + gu + dong);
            userVo.setBirth(year + month + day);
            userVo.setManagerIdentified(managerIdentified);
            userVo.setShopNo(shopNo);

            shopVo.setNo(shopNo);
            shopVo.setAddress(marketAddress);
            shopVo.setNewAddress(marketNewAddress);
            shopVo.setName(marketName);
            shopVo.setPhone(marketPhoneNumber);
            shopVo.setPicture(imageURL);
            shopVo.setIntroduce(marketIntroduce);

            if (longitude == null) {
                shopVo.setLongitude(Double.parseDouble("0.0"));
            } else {
                shopVo.setLongitude(Double.parseDouble(longitude));
            }
            if (latitude == null) {
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
            Log.d("사업자", "회원정보수정 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
            super.onSuccess(userVo);
            finish();
        }
    }
}
