package com.ff.modealapplication.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ff.modealapplication.R;

public class Frame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시
    }
    // ********************* 돋보기 필요 없는 경우 지우기 *********************
    // 돋보기 표시
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 돋보기 클릭시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "눌러", Toast.LENGTH_SHORT).show();
        return true;
    }
    // ********************* 돋보기 필요 없는 경우 지우기 *********************
}
