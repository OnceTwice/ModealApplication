package com.ff.modealapplication.app.ui.item;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bit-desktop on 2017-01-19.
 */

public class ItemInsertActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemService itemService = new ItemService();
    private int indexSingleChoiceSelected = 0;                                                // 상품 카테고리 다이얼로그
    private Long categoryNo;

    // 유통기한 (날짜ㆍ시간)
    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;

    ItemListAsyncTask itemListAsyncTask;

    // 이미지 업로드
    private Uri uri;
    private Bitmap bitmap;
    private Bitmap final_bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_insert);                                                     // 입력된 레이아웃의 대한 클래스

        // 유통기한 날짜ㆍ시간 텍스트뷰 연결
        dateText = (TextView) findViewById(R.id.item_insert_date_text);
        timeText = (TextView) findViewById(R.id.item_insert_time_text);

        // 현재 날짜와 시간을 가져오기 위한 calender 인스턴스 선언
        Calendar calendar = new GregorianCalendar();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);
        UpdateNow();

        // 등록 버튼 클릭시
        findViewById(R.id.item_insert_button_insert).setOnClickListener(this);

        // 취소 버튼 클릭시
        findViewById(R.id.item_insert_button_cancel).setOnClickListener(this);

        // 업로드 버튼 클릭시
        findViewById(R.id.item_insert_button_upload).setOnClickListener(this);
    }

    // 상품 카테고리 다이얼로그
    public void dialogSingleChoice(View view) {
        new AlertDialog.Builder(this).
                setIcon(R.drawable.ic_choice).
                setTitle("상품 카테고리").
                setSingleChoiceItems(R.array.item_category_list, indexSingleChoiceSelected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogSingleChoice", "" + which);
                        categoryNo = Long.valueOf(which);

                        ItemInsertActivity.this.indexSingleChoiceSelected = which;
                        String[] category = getResources().getStringArray(R.array.item_category_list);
                        String choice = category[which];
                        ((TextView) findViewById(R.id.item_insert_category)).setText(choice);
                    }
                }).
                setCancelable(true).
                setPositiveButton("확인", null).
                show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 날짜 버튼 클릭시 설정 화면 보여줌 (여기서 리스너도 등록)
            case R.id.item_insert_date_text: {
                new DatePickerDialog(ItemInsertActivity.this, DateSetListener, Year, Month, Day).show();
                break;
            }

            // 시간 버튼 클릭시 설정 화면 보여줌 (여기서 리스너도 등록)
            case R.id.item_insert_time_text: {
                new TimePickerDialog(ItemInsertActivity.this, TimeSetListener, Hour, Minute, false).show();
                break;
            }

            // 등록 버튼 클릭시
            case R.id.item_insert_button_insert: {
                new ImageUpload().execute();                                                        // 사진을 imgur에 업로드
                break;
            }

            // 취소 버튼 클릭시
            case R.id.item_insert_button_cancel: {
                Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            // 업로드 버튼 클릭시
            case R.id.item_insert_button_upload: {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select Image"), 1);
                final_bitmap = null;
            }
        }
    }

    // 업로드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
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
        }
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
            progressDialog = new ProgressDialog(ItemInsertActivity.this);
            progressDialog.setMessage("Upload...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            itemListAsyncTask = new ItemListAsyncTask();                                     // 생성
            itemListAsyncTask.execute();                                                     // 실행

//            Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class);          // 경로 설정해주고
//            startActivity(intent);                                                              // 여기서 이동하고
            finish();                                                                           // 액티비티를 종료
            new Thread() {
                public void run() {
                    MessagingService.send(((TextView) findViewById(R.id.item_insert_name)).getText().toString() + " 상품이 등록되었습니다.", // 제목
                            LoginPreference.getValue(getApplicationContext(), "name") + " 매장의 " + ((TextView) findViewById(R.id.item_insert_name)).getText().toString() + " 상품이 등록되었습니다.", // 내용
                            "bs" + (Long) LoginPreference.getValue(getApplicationContext(), "shopNo")); // 알림 매장번호
                }
            }.start();

        }
    }

    // 날짜 클릭시
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {      // 사용자가 입력한 값을 가져온뒤
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

        dateText.setText(String.format("%d/%d/%d", Year, Month + 1, Day));
        timeText.setText(String.format("%d:%d", Hour, Minute));

        String expDate = dateText.getText().toString() + " " + timeText.getText().toString();
        Log.w("----------", expDate);
    }

    private class ItemListAsyncTask extends SafeAsyncTask<ItemVo> {

        public ItemVo call() throws Exception {
            EditText nameInsert = (EditText) findViewById(R.id.item_insert_name);
            String item_name = nameInsert.getText().toString();

            EditText oriInsert = (EditText) findViewById(R.id.item_insert_ori_price);
            Long ori_price = Long.parseLong(oriInsert.getText().toString());

            EditText countInsert = (EditText) findViewById(R.id.item_insert_count);
            Long count = Long.parseLong(countInsert.getText().toString());

            EditText priceInsert = (EditText) findViewById(R.id.item_insert_price);
            Long price = Long.parseLong(priceInsert.getText().toString());

            EditText discountInsert = (EditText) findViewById(R.id.item_insert_discount);
            Long discount = Long.parseLong(discountInsert.getText().toString());

            TextView dateText = (TextView) findViewById(R.id.item_insert_date_text);
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.item_insert_time_text);
            String exp_time = timeText.getText().toString();

            Long shopNo = (Long) LoginPreference.getValue(getApplicationContext(), "shopNo");

            Long itemCategoryNo = categoryNo + 1;                                                 // 카테고리가 0 부터라서 +1 추가

            itemService.itemInsert(item_name, ori_price, count, price, exp_date + " " + exp_time, discount, shopNo, itemCategoryNo, url);

            return null;                                                                          // 상품 등록이라서 리턴할 값 없음
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            //super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(ItemVo itemVos) throws Exception {
            //super.onSuccess(itemVos);
        }
    }
}
