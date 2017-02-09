package com.ff.modealapplication.app.ui.item;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by yeo on 2017-01-30.
 */

public class ItemModifyActivity extends AppCompatActivity implements View.OnClickListener {

    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;
    String expDate;

    private ItemService itemService = new ItemService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_modify);

        // 유통기한 날짜ㆍ시간 텍스트뷰 연결
        dateText = (TextView) findViewById(R.id.date_text);
        timeText = (TextView) findViewById(R.id.time_text);

        // 현재 날짜와 시간을 가져오기 위한 calender 인스턴스 선언
        Calendar calendar = new GregorianCalendar();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        UpdateNow();

        // 수정 버튼 클릭시
        findViewById(R.id.button_modify).setOnClickListener(this);

        // 취소 버튼 클릭시
        findViewById(R.id.button_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 날짜 버튼 클릭시 설정 화면 보여줌
            case R.id.date_text: {
                // 여기서 리스너도 등록함
                new DatePickerDialog(ItemModifyActivity.this, DateSetListener, Year, Month, Day).show();
                break;
            }

            // 시간 버튼 클릭시 설정 화면 보여줌
            case R.id.time_text: {
                new TimePickerDialog(ItemModifyActivity.this, TimeSetListener, Hour, Minute, false).show();
                break;
            }

            // 수정 버튼 클릭시
            case R.id.button_modify: {
                ItemListAsyncTask itemListAsyncTask = new ItemListAsyncTask();
                itemListAsyncTask.execute();

                Intent intent = new Intent(ItemModifyActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            // 취소 버튼 클릭시
            case R.id.button_cancel: {
                finish();
                break;
            }
        }
    }

    // 날짜 버튼 클릭시
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
            // 사용자가 입력한 값을 가져온뒤
            Year = year;
            Month = monthOfyear;
            Day = dayOfMonth;
            // 텍스트뷰의 값을 업데이트함
            UpdateNow();
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

    private class ItemListAsyncTask extends SafeAsyncTask<List<ItemVo>> {

        public List<ItemVo> call() throws Exception {
            EditText nameModify = (EditText) findViewById(R.id.item_name);
            Log.d("name : ", nameModify.getText().toString());
            String item_name = nameModify.getText().toString();

            EditText oriModify = (EditText) findViewById(R.id.ori_price);
            Log.d("ori_price : ", oriModify.getText().toString());
            Long ori_price = Long.parseLong(oriModify.getText().toString());

            EditText countModify = (EditText) findViewById(R.id.count);
            Log.d("count : ", countModify.getText().toString());
            Long count = Long.parseLong(countModify.getText().toString());

            EditText priceModify = (EditText) findViewById(R.id.price);
            Log.d("price : ", priceModify.getText().toString());
            Long price = Long.parseLong(priceModify.getText().toString());

            EditText discountModify = (EditText) findViewById(R.id.discount);
            Log.d("discount : ", discountModify.getText().toString());
            Long discount = Long.parseLong(discountModify.getText().toString());

            TextView dateText = (TextView) findViewById(R.id.date_text);
            Log.d("exp_date : ", dateText.getText().toString());
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.time_text);
            Log.d("exp_date : ", timeText.getText().toString());
            String exp_time = timeText.getText().toString();

            List<ItemVo> list = itemService.itemModify(item_name, ori_price, count, price, "abcde", discount);

            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<ItemVo> itemVos) throws Exception {
//            super.onSuccess(itemVos);
        }
    }
}