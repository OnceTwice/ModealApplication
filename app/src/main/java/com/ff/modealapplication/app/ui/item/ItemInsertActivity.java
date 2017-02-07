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

import static com.ff.modealapplication.R.id.date_text;
import static com.ff.modealapplication.R.id.time_text;

/**
 * Created by bit-desktop on 2017-01-19.
 */

public class ItemInsertActivity extends AppCompatActivity implements View.OnClickListener {

    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;
    String expDate;

    private ItemService itemService = new ItemService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_insert); // ← 입력된 레이아웃의 대한 클래스

        // 텍스트뷰 2개 연결
        dateText = (TextView)findViewById(date_text);
        timeText = (TextView)findViewById(time_text);

        // 현재 날짜와 시간을 가져오기 위한 calender 인스턴스 선언
        Calendar calendar = new GregorianCalendar();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        UpdateNow();

        // 등록 버튼 클릭시
        findViewById(R.id.button_insert).setOnClickListener(this);

        // 취소 버튼 클릭시
        findViewById(R.id.button_cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 날짜 버튼 클릭시 설정 화면 보여줌
            case R.id.button_date: {
                // 여기서 리스너도 등록함
                new DatePickerDialog(ItemInsertActivity.this, DateSetListener, Year, Month, Day).show();
                break;
            }

            // 시간 버튼 클릭시 설정 화면 보여줌
            case R.id.button_time: {
                new TimePickerDialog(ItemInsertActivity.this, TimeSetListener, Hour, Minute, false).show();
                break;
            }

            case R.id.button_insert: { // 버튼 클릭시
                ItemListAsyncTask itemListAsyncTask = new ItemListAsyncTask();
                itemListAsyncTask.execute();

                Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class); // 경로 설정해주고
                startActivity(intent); // 여기서 이동하고
                finish(); // 이 액티비티를 종료해줌
                break;
            }

            case R.id.button_cancel: {
                Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class);
                startActivity(intent);
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

    void UpdateNow(){
        dateText.setText(String.format("%d/%d/%d", Year, Month + 1, Day));
        timeText.setText(String.format("%d:%d", Hour, Minute));
        String expDate = dateText.getText().toString() + " " + timeText.getText().toString();
        Log.w("----------", expDate);
    }

    private class ItemListAsyncTask extends SafeAsyncTask<List<ItemVo>> {

        public List<ItemVo> call() throws Exception {
            EditText editText1 = (EditText) findViewById(R.id.item_name);
            Log.d("name : ", editText1.getText().toString());
            String item_name = editText1.getText().toString();

            EditText editText2 = (EditText) findViewById(R.id.ori_price);
            Log.d("ori_price : ", editText2.getText().toString());
            Long ori_price = Long.parseLong(editText2.getText().toString()); // 스트링을 롱으로 변경해줌

            EditText editText3 = (EditText) findViewById(R.id.count);
            Log.d("count : ", editText3.getText().toString());
            Long count = Long.parseLong(editText3.getText().toString());

            EditText editText4 = (EditText) findViewById(R.id.price);
            Log.d("price : ", editText4.getText().toString());
            Long price = Long.parseLong(editText4.getText().toString());

            EditText editText5 = (EditText) findViewById(R.id.discount);
            Log.d("discount : ", editText5.getText().toString());
            Long discount = Long.parseLong(editText5.getText().toString());

            TextView dateText = (TextView) findViewById(R.id.date_text);
            Log.d("exp_date : ", dateText.getText().toString());
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.time_text);
            Log.d("exp_date : ", timeText.getText().toString());
            String exp_time = timeText.getText().toString();

            List<ItemVo> list = itemService.itemInsert(item_name, ori_price, count, price, exp_date, discount);

            return list;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            //super.onException(e);
            throw new RuntimeException(e);
        }

        @Override
        protected void onSuccess(List<ItemVo> itemVos) throws Exception {
            //super.onSuccess(itemVos);
        }
    }
}
