package com.ff.modealapplication.app.ui.item;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.domain.ItemVo;
import com.ff.modealapplication.app.core.util.Base;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by yeo on 2017-01-30.
 */

public class ItemModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemService itemService = new ItemService();
    private int indexSingleChoiceSelected = 0;
    private Long categoryNo;

    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;

    ItemModifyActivity.ItemModifyTask itemModifyTask;
    ItemModifyActivity.ItemModifyUpdateTask itemModifyUpdateTask;

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

        // 버튼(취소 / 수정) 클릭시
        findViewById(R.id.item_modify_button_modify).setOnClickListener(this);
        findViewById(R.id.item_modify_button_cancel).setOnClickListener(this);

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

            // 수정 버튼 클릭시
            case R.id.item_modify_button_modify: {
                itemModifyUpdateTask = new ItemModifyUpdateTask();
                itemModifyUpdateTask.execute();

//                Intent intent = new Intent(ItemModifyActivity.this, ItemActivity.class);          // 경로 설정해주고
//                startActivity(intent);                                                              // 여기서 이동
//                finish();
                break;
            }

            // 취소 버튼 클릭시
            case R.id.item_modify_button_cancel: {
//                Intent intent = new Intent(ItemModifyActivity.this, ItemActivity.class);
//                startActivity(intent);
                finish();
                break;
            }

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
        }
    }

    // 날짜 버튼 클릭시
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {      // 사용자가 입력한 값(날짜)을 가져온뒤
            Year = year;
            Month = monthOfyear;
            Day = dayOfMonth;
            UpdateNow();                                                                            // 텍스트뷰의 값을 업데이트함
        }
    };

    // 시간 버튼 클릭시
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
            String choice = category[((int)itemVo.getItemCategoryNo()) - 1 ];
            categoryNo = (itemVo.getItemCategoryNo()) - 1 ;                                       // 상품 화면 출력시 해당 categoryNo 값 기본 세팅

            ((TextView) findViewById(R.id.item_modify_category)).setText(choice);                           // 카테고리
            ((EditText) findViewById(R.id.item_modify_name)).setText(itemVo.getName().toString());           // 상품명
            ((EditText) findViewById(R.id.item_modify_count)).setText(itemVo.getCount() + "");              // 수량
            ((EditText) findViewById(R.id.item_modify_ori_price)).setText((itemVo.getOriPrice()) + "");     // 원가
            ((EditText) findViewById(R.id.item_modify_price)).setText((itemVo.getPrice()) + "");            // 판매가
            ((EditText) findViewById(R.id.item_modify_discount)).setText(itemVo.getDiscount() + "");        // 할인율
            ((TextView) findViewById(R.id.item_modify_date_text)).setText(year + "/" + month + "/" + day);  // 날짜
            ((TextView) findViewById(R.id.item_modify_time_text)).setText(hour + ":" + minute);             // 시간
            ImageLoader.getInstance().displayImage(Base.url + "modeal/shop/images/" + itemVo.getPicture(),
                    (ImageView) findViewById(R.id.item_modify_image_view), displayImageOption);         // 상품이미지
        }
    }

    // 상품 수정 - 업데이트(갱신) ------------------------------------------------------------------
    public class ItemModifyUpdateTask extends SafeAsyncTask <ItemVo> {

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

            EditText discountModify = (EditText) findViewById(R.id.item_modify_discount);
            Long discount = Long.parseLong(discountModify.getText().toString());

            TextView dateText = (TextView) findViewById(R.id.item_modify_date_text);
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.item_modify_time_text);
            String exp_time = timeText.getText().toString();

            Long itemCategoryNo =  categoryNo + 1;

            itemService.itemModifyUpdate(no, item_name, ori_price, count, price, exp_date + " " + exp_time, discount, itemCategoryNo);

            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            Log.d("errr","ee"+e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(ItemVo itemVo) throws Exception {
//            super.onSuccess(itemVos);
        }
    }
}