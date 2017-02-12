package com.ff.modealapplication.app.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;

public class SearchMapRangeActivity extends Activity implements View.OnClickListener {

    public static Activity FinishSearchMapRangeActivity;

    private Button mConfirm, mCancel, findAddress;
    private String longitude = null;
    private String latitude = null;
    private String range = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FinishSearchMapRangeActivity = SearchMapRangeActivity.this;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 다이얼로그테마를 쓰므로 여기서 액션바를 없애준다
        setContentView(R.layout.activity_search_map_range);

        if (this.getIntent() != null) {  // 넘어오는 값이 있는 경우
            Intent setIntent = new Intent(this.getIntent());
            ((TextView) findViewById(R.id.textView_address)).setText(setIntent.getStringExtra("title")); // 받아온 주소값을 주소 : 에 입력
            longitude = setIntent.getStringExtra("longitude"); // 경도(세로) 저장
            latitude = setIntent.getStringExtra("latitude"); // 위도(가로) 저장

            setContent();
        }

        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // 반경 설정

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ((TextView) findViewById(R.id.textView_lange)).setText(progress * 30 + "m");
                range = String.valueOf(progress * 10); // 반경 설정한 값 (미터단위)
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    private void setContent() {
        findAddress = (Button) findViewById(R.id.button_findAddress); // 주소찾기 버튼
        mConfirm = (Button) findViewById(R.id.btnConfirm); // 검색 버튼
        mCancel = (Button) findViewById(R.id.btnCancel); // 취소 버튼

        findAddress.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm: // 검색 버튼
                if ((((TextView) findViewById(R.id.textView_address)).getText().toString()).equals("")) { // 주소 : 에 값이 없는 경우 (주소찾기를 안해서 주소값이 없는 경우)
                    Toast.makeText(getApplicationContext(), "'주소찾기' 버튼을 실행 해 주세요", Toast.LENGTH_SHORT).show();
                } else { // 주소 : 에 값이 있는 경우
                    Intent intentResult = new Intent(this, SearchShopToPointActivity.class); // 검색한 주소의 반경 크기에 해당하는 지도를 띄움
                    intentResult.putExtra("longitude", longitude); // 경도
                    intentResult.putExtra("latitude", latitude); // 위도
                    intentResult.putExtra("title", ((TextView) findViewById(R.id.textView_address)).getText()); // 주소
                    intentResult.putExtra("range", range); // 반경
                    startActivity(intentResult);
                }
                break;
            case R.id.button_findAddress: // 주소찾기 버튼
                if ((((EditText) findViewById(R.id.editText_address)).getText().toString()).equals("")) { // 검색할 주소를 입력 안했을때
                    Toast.makeText(getApplicationContext(), "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else { // 검색할 주소를 입력했을때
                    Intent intent = new Intent(this, AddressListActivity.class);
                    EditText editText = (EditText) findViewById(R.id.editText_address); // 입력한 주소
                    String addr = editText.getText().toString();
                    intent.putExtra("addr", addr); // 입력한 주소를 AddressListActivity로 넘김
                    startActivityForResult(intent, 1000);
                }
                break;

            case R.id.btnCancel: // 취소 버튼
                this.finish(); // 액티비티 닫기
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) { // startActivityForResult 가 실패 했을 경우
            //Toast.makeText(MainActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == 1000) { // 성공해서 값 받아올 경우
            ((TextView) findViewById(R.id.textView_address)).setText(data.getStringExtra("title"));
            longitude = data.getStringExtra("longitude");
            latitude = data.getStringExtra("latitude");


            //Toast.makeText(MainActivity.this, "결과 : " + resultMsg, Toast.LENGTH_SHORT).show();
        } else { // 넌 뭐냐...
            //Toast.makeText(MainActivity.this, "REQUEST_ACT가 아님", Toast.LENGTH_SHORT).show();
        }
    }
}
