package com.ff.modealapplication.app.ui.join;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.domain.ShopVo;
import com.ff.modealapplication.app.core.domain.UserVo;
import com.ff.modealapplication.app.core.service.EmailCheckService;
import com.ff.modealapplication.app.core.service.OwnerJoinService;
import com.ff.modealapplication.app.core.service.map.JoinMapInfoVo;
import com.ff.modealapplication.app.ui.map.SearchShopToJoinActivity;
import com.ff.modealapplication.app.ui.map.SearchToShopInMapActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.ff.modealapplication.app.core.util.Base.LeafYear;
import static com.ff.modealapplication.app.core.util.Base.returnDay;

public class OwnerJoinFragment extends Fragment {
    private OwnerJoinService ownerJoinService = new OwnerJoinService();
    private EmailCheckService emailCheckService = new EmailCheckService();

    private EditText etID;
    private Button btnDuplicationID;
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

    private Button btnSubmit;
    private Button btnCancel;

    // 이미지 업로드
    private Uri uri;
    private Bitmap shop_bitmap;
    private static final int RESULT_SELECT_IMAGE = 1;

    String id = "";
    String password = "";
    String gender = "";

    String city = "";
    String gu = "";
    String dong = "";

    String year = "";
    String month = "";
    String day = "";
    int maxDay = 0;

    String marketName = "";
    String marketAddress = "";
    String marketNewAddress = "";
    String marketPhoneNumber = "";
    String imageURL = "";
    String marketIntroduce = "";
    String longitude = "";
    String latitude = "";

    final int[] flag = {0};

    int emailFlag = 1;
    int allCheck = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OwnerJoinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerJoinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerJoinFragment newInstance(String param1, String param2) {
        OwnerJoinFragment fragment = new OwnerJoinFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {       // Fragment가 생성될 때 호출되는 부분
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {        // SearchShopToJoinActivity.java에서 보낸 데이터를 받아옴
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000) {
            if (flag[0] == 0) {
                Log.d("tttttttt", "bbbbbbbbb");
                JoinMapInfoVo joinMapInfoVo = (JoinMapInfoVo) data.getSerializableExtra("joinMapInfoVo");

                etMarketName.setText(joinMapInfoVo.getTitle());         // 매장명
                etMarketAddress.setText(joinMapInfoVo.getAddress());    // 매장주소
                marketNewAddress = joinMapInfoVo.getNewAddress();
                etMarketPhoneNumber.setText(joinMapInfoVo.getPhone());  // 매장전화번호
                imageURL = joinMapInfoVo.getImageUrl();
                longitude = joinMapInfoVo.getLongitude();
                latitude = joinMapInfoVo.getLatitude();

                Log.d("onActivityResult 이미지", imageURL);
//                ImageLoader.getInstance().displayImage(imageURL, (ImageView)getView().findViewById(R.id.marketImage));        // 대안을 찾음

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
//                Log.d("위치위치위치", longitude + "=======" + latitude);
//                Toast.makeText(getActivity(), "longitude : " + longitude + ", latitude : " + latitude, Toast.LENGTH_SHORT).show();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    // onCreate 후에 화면을 구성할 때 호출되는 부분
        View view = inflater.inflate(R.layout.fragment_owner_join, container, false);

        etID = (EditText) view.findViewById(R.id.etOwnerID);
        btnDuplicationID = (Button) view.findViewById(R.id.btnOwnerIDDuplicationCheck);
        etPassword = (EditText) view.findViewById(R.id.etOwnerPassword);
        etPasswordConfirm = (EditText) view.findViewById(R.id.etOwnerPasswordConfirm);

        group = (RadioGroup) view.findViewById(R.id.radioGroupOwnerGender);
        man = (RadioButton) view.findViewById(R.id.OwnerRadio_man);
        woman = (RadioButton) view.findViewById(R.id.OwnerRadio_woman);

        etCity = (EditText) view.findViewById(R.id.etOwnerCity);
        etGu = (EditText) view.findViewById(R.id.etOwnerGu);
        etDong = (EditText) view.findViewById(R.id.etOwnerDong);

        etYear = (EditText) view.findViewById(R.id.etOwnerYear);
        spinnerMonth = (Spinner) view.findViewById(R.id.spOwnerMonth);
        etDay = (EditText) view.findViewById(R.id.etOwnerDay);

        etMarketName = (EditText) view.findViewById(R.id.etOwnerMarketName);
        btnMarketSearch = (Button) view.findViewById(R.id.btnOwnerAddressSearch);

        etMarketAddress = (EditText) view.findViewById(R.id.etOwnerMarketAddress);

        etMarketPhoneNumber = (EditText) view.findViewById(R.id.etOwnerMarketPhoneNumber);

        imageView = (ImageView) view.findViewById(R.id.marketImage);        // 이미지

        etMarketIntroduce = (EditText) view.findViewById(R.id.etOwnerMarketIntroduce);

        btnSubmit = (Button) view.findViewById(R.id.btnOwnerSubmit);
        btnCancel = (Button) view.findViewById(R.id.btnOwnerCancel);

        btnDuplicationID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EmailDuplicationCheck().execute();
            }
        });

        /********  성별입력(최초선택)    ********/
        if (man.isChecked()) {
            gender = "man";
            Log.d("사업자 젠더젠더12121212", gender);
        } else if (woman.isChecked()) {
            gender = "woman";
            Log.d("사업자 젠더젠더13131313", gender);
        }

        /********  성별입력(변경)    ********/
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.OwnerRadio_man: {
                        gender = "man";
                        Log.d("사업자 젠더젠더", gender);
                        break;
                    }
                    case R.id.OwnerRadio_woman: {
                        gender = "woman";
                        Log.d("사업자 젠더젠더", gender);
                        break;
                    }
                }
            }
        });

        /********   생년월일입력    ********/
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                month = (String) adapterView.getItemAtPosition(position);
//                Log.d("=========month", month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("------>", "onNothingMonthSelected");
            }
        });

        /******************************************* 사용자 & 사업자 경계 **************************************************/

        btnMarketSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String items[] = {"매장명으로 검색", "지도에서 직접 찾기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            Intent intentToMap = new Intent(OwnerJoinFragment.this.getActivity(), SearchShopToJoinActivity.class);
                            startActivityForResult(intentToMap, 1000);
                        } else if (flag[0] == 1) {
                            Intent intentToMap = new Intent(OwnerJoinFragment.this.getActivity(), SearchToShopInMapActivity.class);
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckedJoin();            // 회원가입 체크 메소드 호출

//                new ImageUpload().execute(); // 이미지 업로드가 이루어진 후 성공하면 회원가입 AsyncTask가 이루어짐
                new FetchOwnerListAsyncTask().execute();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Log.d("사업자 회원가입", "취소!!!!!");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void isCheckedJoin() {
        Calendar cal = Calendar.getInstance();

        // 아이디 입력 확인
        if (TextUtils.isEmpty(etID.getText().toString())) {
            etID.setError("이메일을 입력하세요");
            etID.requestFocus();
            return;
        } else if (!isEmailValid(etID.getText().toString())) {
            etID.setError("이메일 형식이 아닙니다");
            etID.requestFocus();
            return;
        }

        if( !(emailFlag == 0) ) {
            Toast.makeText(OwnerJoinFragment.this.getActivity(), "중복검사를 진행해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(OwnerJoinFragment.this.getActivity(), "사업자 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etPasswordConfirm.setText("");
            etPassword.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etCity.getText().toString())) {
            etCity.setError("시(도)를 입력하세요");
            etCity.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etGu.getText().toString())) {
            etGu.setError("구(군)을 입력하세요");
            etGu.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etDong.getText().toString())) {
            etDong.setError("동(읍/면/리)를 입력하세요");
            etDong.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etYear.getText().toString())) {
            etYear.setError("년도를 입력하세요");
            etYear.requestFocus();
            return;
        }

        if(( (Integer.parseInt((etYear.getText().toString()))) / 1000) == 0) {
            etYear.setError("태어난 년도 4자리를 정확하게 입력하세요.");
            etYear.requestFocus();
            return;
        }

        if( (Integer.parseInt((etYear.getText().toString()))) >= cal.get(cal.YEAR) ) {
            etYear.setError("미래에서 오셨군요^^");
            etYear.requestFocus();
            return;
        }

        if(spinnerMonth.getSelectedItem().toString().equals("월")) {
            Toast.makeText(OwnerJoinFragment.this.getActivity(), "월을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(etDay.getText().toString())) {
            etDay.setError("일을 입력하세요");
            etDay.requestFocus();
            return;
        }

        if(spinnerMonth.getSelectedItem().toString().equals("2")) {             // 월별 날짜 체크
            maxDay = LeafYear(Integer.parseInt((etYear.getText().toString())));
        } else {
            maxDay = returnDay(Integer.parseInt(spinnerMonth.getSelectedItem().toString()));
        }

        if(Integer.parseInt(etDay.getText().toString()) > maxDay) {
            Toast.makeText(OwnerJoinFragment.this.getActivity(), "정말이신가요?", Toast.LENGTH_SHORT).show();
            etDay.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etMarketName.getText().toString())) {
            etMarketName.setError("매장 이름을 입력하세요");
            etMarketName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etMarketAddress.getText().toString())) {
            etMarketAddress.setError("매장주소를 입력하세요");
            etMarketAddress.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(etMarketPhoneNumber.getText().toString())) {
            etMarketPhoneNumber.setError("매장 전화번호를 입력하세요");
            etMarketPhoneNumber.requestFocus();
            return;
        }

        allCheck = 0;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    // 이미지 업로드 ======================================================= 여기부터 =================================================
    // 업로드용(사진의 절대 경로 구하기)
    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
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
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("상품을 등록하고 있습니다...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
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

            if(allCheck == 0) {
                new FetchOwnerListAsyncTask().execute();
            }
        }
    }

    // 이미지 업로드 ======================================================= 여기까지 =================================================

    private class EmailDuplicationCheck extends SafeAsyncTask<Integer> {
        @Override
        public Integer call() throws Exception {
            id = etID.getText().toString();

            Integer check = emailCheckService.checkedEmail(id);

            System.out.println("사업자 EmailDuplicationCheck : " + check);

            return check;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("아이디 중복 체크", "사업자 서비스 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(Integer check) throws Exception {
            if ((check == null) || (check == 0)) {
                Toast.makeText(OwnerJoinFragment.this.getActivity(), "중복검사 완료 \n 회원가입을 진행하세요", Toast.LENGTH_SHORT).show();
                emailFlag = 0;
            } else {
                Toast.makeText(OwnerJoinFragment.this.getActivity(), "중복입니다!!!", Toast.LENGTH_SHORT).show();
                etID.setText("");
                etID.requestFocus();
                emailFlag = 1;
            }

            Log.d("아이디 중복 체크", "성공!!!!!");
        }
    }

    private class FetchOwnerListAsyncTask extends SafeAsyncTask<UserVo> {
        private UserVo userVo = new UserVo();
        private ShopVo shopVo = new ShopVo();

        @Override
        public UserVo call() throws Exception {
            Log.d("사업자 패치 메소드", "들어옴!!!!!");

            id = etID.getText().toString();
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

            userVo.setId(id);
            userVo.setPassword(password);
            userVo.setGender(gender);
            userVo.setLocation(city + gu + dong);
            userVo.setBirth(year + month + day);
            userVo.setManagerIdentified(2L);     // 2 : 사업자 고유 번호

            Log.d("id======", id);
            Log.d("password======", password);

            Log.d("city======", city);
            Log.d("gu======", gu);
            Log.d("dong======", dong);

            Log.d("year======", year);
            Log.d("month======", month);
            Log.d("day======", day);

            shopVo.setName(marketName);
            shopVo.setAddress(marketAddress);
            shopVo.setNewAddress(marketNewAddress);
            shopVo.setPhone(marketPhoneNumber);
            shopVo.setPicture(imageURL);
            shopVo.setIntroduce(marketIntroduce);

            Log.d("marketName===", marketName);
            Log.d("marketAddress===", marketAddress);
            Log.d("marketNewAddress===", marketNewAddress);
            Log.d("marketPhoneNumber===", marketPhoneNumber);
            Log.d("imageURL===", imageURL);
            Log.d("marketIntroduce===", marketIntroduce);
            Log.d("longitude===", longitude);
            Log.d("latitude===", latitude);

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
            Log.d("longitude===", longitude);
            Log.d("latitude===", latitude);

            UserVo ownerVo = ownerJoinService.fetchOwnerList(userVo, shopVo);

            return ownerVo;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("사업자", "서비스 에러뜸!!!" + e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {
//            super.onSuccess(userVo);
            Log.d("사업자 성공", "성공해쓰요");
            getActivity().finish();
        }
    }
}
