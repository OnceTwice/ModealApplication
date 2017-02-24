package com.ff.modealapplication.app.ui.item;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.domain.ItemVo;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.message.MessagingService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yeo on 2017-01-30.
 */

public class ItemModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemService itemService = new ItemService();
    private int indexSingleChoiceSelected = 0;                                                // 상품 카테고리 다이얼로그
    private Long categoryNo;

    // 유통기한 (날짜ㆍ시간)
    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;

    ItemModifyActivity.ItemModifyTask itemModifyTask;                                             // 상품 기존 정보 출력
    ItemModifyActivity.ItemModifyUpdateTask itemModifyUpdateTask;                                // 상품 정보 수정

    // 이미지 업로드
    private Uri uri;
    private Bitmap bitmap;
    private Bitmap final_bitmap;
    public ImageView item_modify_image_view;
    private static final int RESULT_SELECT_IMAGE = 1;
    private String timestamp;

    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_modify);

        // 유통기한 날짜ㆍ시간 텍스트뷰 연결
        dateText = (TextView) findViewById(R.id.item_modify_date_text);
        timeText = (TextView) findViewById(R.id.item_modify_time_text);

        // 현재 날짜와 시간을 가져오기 위한 calender 인스턴스 선언
        Calendar calendar = new GregorianCalendar();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);
        UpdateNow();

        // 수정 버튼 클릭시
        findViewById(R.id.item_modify_button_modify).setOnClickListener(this);

        // 취소 버튼 클릭시
        findViewById(R.id.item_modify_button_cancel).setOnClickListener(this);

        // 업로드 버튼 클릭시
        findViewById(R.id.item_modify_button_upload).setOnClickListener(this);

        itemModifyTask = new ItemModifyTask();
        itemModifyTask.execute();
    }

    // 상품 카테고리 -------------------------------------------------------------------------------
    public void dialogSingleChoice(View view) {
        new AlertDialog.Builder(this).
                setIcon(R.drawable.ic_choice).
                setTitle("상품 카테고리").
                setSingleChoiceItems(R.array.item_category_list, indexSingleChoiceSelected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogSingleChoice", "" + which);
                        categoryNo = Long.valueOf(which);

                        ItemModifyActivity.this.indexSingleChoiceSelected = which;
                        String[] category = getResources().getStringArray(R.array.item_category_list);
                        String choice = category[which];
                        ((TextView) findViewById(R.id.item_modify_category)).setText(choice);
                    }
                }).
                setCancelable(true).
                setPositiveButton("확인", null).
                show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 날짜 버튼 클릭시 설정 화면 출력 (여기서 리스너도 등록)
            case R.id.item_modify_date_text: {
                new DatePickerDialog(ItemModifyActivity.this, DateSetListener, Year, Month, Day).show();
                break;
            }

            // 시간 버튼 클릭시 설정 화면 출력 (여기서 리스너도 등록)
            case R.id.item_modify_time_text: {
                new TimePickerDialog(ItemModifyActivity.this, TimeSetListener, Hour, Minute, false).show();
                break;
            }

            // 수정 버튼 클릭시
            case R.id.item_modify_button_modify: {
                new ImageUpload().execute();                                                        // 사진을 imgur에 업로드
                break;
            }

            // 취소 버튼 클릭시
            case R.id.item_modify_button_cancel: {
                finish();
                break;
            }

            // 업로드 버튼 클릭시
            case R.id.item_modify_button_upload: {
                 Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                galleryIntent.setType("gallery*//*");
                startActivityForResult(galleryIntent.createChooser(galleryIntent, "Select Image"), RESULT_SELECT_IMAGE);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image*//*");
////                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent.createChooser(intent, "Select Image"), 1);
                final_bitmap = null;
            }
        }
    }

    // 이미지 업로드 -------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {

            uri = data.getData();

            bitmap = BitmapFactory.decodeFile(getPath(uri)); // uri → bitmap 변환
            // 이미지의 가로길이 200으로 맞춰 이미지 크기 조절
            int resizeWidth = 200;
            double aspectRatio = (double)bitmap.getHeight() / bitmap.getWidth();
            int targetHeight = (int) (resizeWidth * aspectRatio);
            final_bitmap = Bitmap.createScaledBitmap(bitmap, resizeWidth, targetHeight, false);
            uri = getImageUri(getApplicationContext(), final_bitmap);

            ((ImageView) findViewById(R.id.item_modify_image_view)).setImageURI(uri);

            Log.w("path?", uri.getPath() + " << uri.getPath(), " + uri + " << uri, " + getPath(uri) + " << getPath(uri)");
        }

        // 구버전용
/*        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            // 같은듯...
            bitmap = BitmapFactory.decodeFile(getPath(uri));
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 1000 || height > 1000) {
                width = width / 4;
                height = height / 4;
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            ((ImageView) findViewById(R.id.item_insert_image_view)).setImageBitmap(bitmap);
            Log.w("path?", uri.getPath() + " << uri.getPath(), " + uri + " << uri, " + getPath(uri) + " << getPath(uri)");
        }*/
    }

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
            progressDialog = new ProgressDialog(ItemModifyActivity.this);
            progressDialog.setMessage("상품을 등록하고 있습니다...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);;
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
            itemModifyUpdateTask = new ItemModifyUpdateTask();                                   // 생성
            itemModifyUpdateTask.execute();                                                      // 실행

            finish();                                                                               // 액티비티를 종료
            new Thread() {
                public void run() {
                    MessagingService.send(((TextView) findViewById(R.id.item_modify_name)).getText().toString() + " 상품이 등록되었습니다.", // 제목
                            LoginPreference.getValue(getApplicationContext(), "name") + " 매장의 " + ((TextView) findViewById(R.id.item_modify_name)).getText().toString() + " 상품이 등록되었습니다.", // 내용
                            "bs" + (Long) LoginPreference.getValue(getApplicationContext(), "shopNo")); // 알림 매장번호
                }
            }.start();
        }
    }

    // 날짜 클릭시 ---------------------------------------------------------------------------------
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {      // 사용자가 입력한 값(날짜)을 가져온뒤
            Year = year;
            Month = monthOfyear;
            Day = dayOfMonth;
            UpdateNow();                                                                            // 텍스트뷰의 값을 업데이트함
        }
    };

    // 시간 클릭시
    TimePickerDialog.OnTimeSetListener TimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Hour = hourOfDay;
            Minute = minute;
            UpdateNow();
        }
    };

    void UpdateNow() {
        dateText.setText(String.format("%d 년 %d 월 %d 일", Year, Month + 1, Day));
        timeText.setText(String.format("%d 시 %d 분 까지", Hour, Minute));
        String expDate = dateText.getText().toString() + " " + timeText.getText().toString();
    }

    // 상품 수정 - 기존에 입력한 정보 출력 ---------------------------------------------------------
    private class ItemModifyTask extends SafeAsyncTask<ItemVo> {

        @Override
        public ItemVo call() throws Exception {                                                   // call 부분이 서버에서 가져온 값
            return itemService.itemModify(getIntent().getLongExtra("no", -1));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("Error : ", e + "!!!");
            super.onException(e);
        }

        @Override
        protected void onSuccess(ItemVo itemVo) throws Exception {                               // 성공하면 여기로
            StringTokenizer tokens = new StringTokenizer(itemVo.getExpDate().toString(), "-/: ");
            String year = tokens.nextToken();
            String month = tokens.nextToken();
            String day = tokens.nextToken();
            String hour = tokens.nextToken();
            String minute = tokens.nextToken();

            String[] category = getResources().getStringArray(R.array.item_category_list);       // 상품 카테고리 숫자→한글로 출력
            String choice = category[((int) itemVo.getItemCategoryNo()) - 1];
            categoryNo = (itemVo.getItemCategoryNo()) - 1;                                        // 상품 화면 출력시 해당 categoryNo 값 기본 세팅

            ((TextView) findViewById(R.id.item_modify_category)).setText(choice);                           // 카테고리
            ((EditText) findViewById(R.id.item_modify_name)).setText(itemVo.getName().toString());           // 상품명
            ((EditText) findViewById(R.id.item_modify_count)).setText(itemVo.getCount() + "");              // 수량
            ((EditText) findViewById(R.id.item_modify_ori_price)).setText((itemVo.getOriPrice()) + "");     // 원가
            ((EditText) findViewById(R.id.item_modify_price)).setText((itemVo.getPrice()) + "");            // 판매가
            ((TextView) findViewById(R.id.item_modify_date_text)).setText(year + "/" + month + "/" + day);  // 날짜
            ((TextView) findViewById(R.id.item_modify_time_text)).setText(hour + ":" + minute);             // 시간
            ImageLoader.getInstance().displayImage(itemVo.getPicture(),
                    (ImageView) findViewById(R.id.item_modify_image_view), displayImageOption);         // 상품이미지
        }
    }

    // 상품 수정 - 업데이트(갱신) ------------------------------------------------------------------
    public class ItemModifyUpdateTask extends SafeAsyncTask<ItemVo> {

        public ItemVo call() throws Exception {

            Long no = getIntent().getLongExtra("no", -1);

            EditText nameModify = (EditText) findViewById(R.id.item_modify_name);
            String item_name = nameModify.getText().toString();

            EditText oriModify = (EditText) findViewById(R.id.item_modify_ori_price);
            Long ori_price = Long.parseLong(oriModify.getText().toString());

            EditText countModify = (EditText) findViewById(R.id.item_modify_count);
            Long count = Long.parseLong(countModify.getText().toString());

            EditText priceModify = (EditText) findViewById(R.id.item_modify_price);
            Long price = Long.parseLong(priceModify.getText().toString());

            Long discount = 100 - (long)(Math.ceil((price.doubleValue() / ori_price) * 100));      // 할인율 계산

            TextView dateText = (TextView) findViewById(R.id.item_modify_date_text);
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.item_modify_time_text);
            String exp_time = timeText.getText().toString();

            Long itemCategoryNo = categoryNo + 1;

            itemService.itemModifyUpdate(no, item_name, ori_price, count, price, exp_date + " " + exp_time, discount, itemCategoryNo, url);

            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(ItemVo itemVo) throws Exception {
//            super.onSuccess(itemVos);
        }
    }
}