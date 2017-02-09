package com.ff.modealapplication.app.ui.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_map_range);

        if (this.getIntent() != null) {
            Intent setIntent = new Intent(this.getIntent());
            ((TextView) findViewById(R.id.textView_address)).setText(setIntent.getStringExtra("title"));
            longitude = setIntent.getStringExtra("longitude");
            latitude = setIntent.getStringExtra("latitude");

            setContent();
        }


        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ((TextView) findViewById(R.id.textView)).setText("주변반경 : " + progress * 10);
                range = String.valueOf(progress * 10);
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
        findAddress = (Button) findViewById(R.id.button_findAddress);
        mConfirm = (Button) findViewById(R.id.btnConfirm);
        mCancel = (Button) findViewById(R.id.btnCancel);

        findAddress.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                Intent intentResult = new Intent(this, SearchShopToPointActivity.class);
                Log.d("Rnage=====>", "longitude=" + longitude + ", latitude=" + latitude + ", range=" + range);
                intentResult.putExtra("longitude", longitude);
                intentResult.putExtra("latitude", latitude);
                intentResult.putExtra("title", ((TextView)findViewById(R.id.textView_address)).getText());
                intentResult.putExtra("range", range);
                startActivity(intentResult);

                break;

            case R.id.button_findAddress:

                Intent intent = new Intent(this, AddressListActivity.class);
                EditText editText = (EditText) findViewById(R.id.editText_address);

                String addr = editText.getText().toString();

                intent.putExtra("addr", addr);
                startActivityForResult(intent, 1000);
                break;

            case R.id.btnCancel:
                this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            //Toast.makeText(MainActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == 1000) {
            ((TextView) findViewById(R.id.textView_address)).setText(data.getStringExtra("title"));
            longitude = data.getStringExtra("longitude");
            latitude = data.getStringExtra("latitude");


            //Toast.makeText(MainActivity.this, "결과 : " + resultMsg, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(MainActivity.this, "REQUEST_ACT가 아님", Toast.LENGTH_SHORT).show();
        }
    }
}