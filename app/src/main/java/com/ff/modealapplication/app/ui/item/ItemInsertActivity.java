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
import android.widget.TextView;
import android.widget.TimePicker;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by bit-desktop on 2017-01-19.
 */

public class ItemInsertActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemService itemService = new ItemService();
    private int indexSingleChoiceSelected = 0;

    int Year, Month, Day, Hour, Minute;
    TextView dateText;
    TextView timeText;

    ItemListAsyncTask itemListAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_insert); // ← 입력된 레이아웃의 대한 클래스

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
    }

    // 상품 카테고리
    public void dialogSingleChoice(View view) {
        new AlertDialog.Builder(this).
                setIcon(R.drawable.ic_choice).
                setTitle("상품 카테고리").
                setSingleChoiceItems(R.array.item_category_list, indexSingleChoiceSelected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogSingleChoice", "" + which);

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
            // 날짜 버튼 클릭시 설정 화면 보여줌
            case R.id.item_insert_date_text: {
                // 여기서 리스너도 등록함
                new DatePickerDialog(ItemInsertActivity.this, DateSetListener, Year, Month, Day).show();
                break;
            }

            // 시간 버튼 클릭시 설정 화면 보여줌
            case R.id.item_insert_time_text: {
                new TimePickerDialog(ItemInsertActivity.this, TimeSetListener, Hour, Minute, false).show();
                break;
            }

            // 등록 버튼 클릭시
            case R.id.item_insert_button_insert: {
                itemListAsyncTask = new ItemListAsyncTask();
                itemListAsyncTask.execute();

                Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class); // 경로 설정해주고
                startActivity(intent); // 여기서 이동하고
                finish(); // 이 액티비티를 종료해줌
                break;
            }

            // 취소 버튼 클릭시
            case R.id.item_insert_button_cancel: {
                Intent intent = new Intent(ItemInsertActivity.this, ItemActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    // 날짜 클릭시
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

        dateText.setText(String.format("%d / %d / %d", Year, Month + 1, Day));
        timeText.setText(String.format("%d : %d", Hour, Minute));

        String expDate = dateText.getText().toString() + " " + timeText.getText().toString();
        Log.w("----------", expDate);
    }

    private class ItemListAsyncTask extends SafeAsyncTask<ItemVo> {

        public ItemVo call() throws Exception {
            EditText nameInsert = (EditText) findViewById(R.id.item_insert_name);
            Log.d("name : ", nameInsert.getText().toString());
            String item_name = nameInsert.getText().toString();

            EditText oriInsert = (EditText) findViewById(R.id.item_insert_ori_price);
            Log.d("ori_price : ", oriInsert.getText().toString());
            Long ori_price = Long.parseLong(oriInsert.getText().toString());

            EditText countInsert = (EditText) findViewById(R.id.item_insert_count);
            Log.d("count : ", countInsert.getText().toString());
            Long count = Long.parseLong(countInsert.getText().toString());

            EditText priceInsert = (EditText) findViewById(R.id.item_insert_price);
            Log.d("price : ", priceInsert.getText().toString());
            Long price = Long.parseLong(priceInsert.getText().toString());

            EditText discountInsert = (EditText) findViewById(R.id.item_insert_discount);
            Log.d("discount : ", discountInsert.getText().toString());
            Long discount = Long.parseLong(discountInsert.getText().toString());

            TextView dateText = (TextView) findViewById(R.id.item_insert_date_text);
            Log.d("exp_date : ", dateText.getText().toString());
            String exp_date = dateText.getText().toString();

            TextView timeText = (TextView) findViewById(R.id.item_insert_time_text);
            Log.d("exp_date : ", timeText.getText().toString());
            String exp_time = timeText.getText().toString();

            itemService.itemInsert(item_name, ori_price, count, price, exp_date + " " + exp_time, discount);

            return null;
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
