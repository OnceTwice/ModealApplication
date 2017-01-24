package com.ff.modealapplication.app.ui.Main;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ff.modealapplication.R;


/**
 * Created by BIT on 2017-01-24.
 */

public class BaseActivity extends ListActivity implements NavigationView.OnNavigationItemSelectedListener {
    AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이제 그냥 이거 안 지우고 써도 될것 같음... (170124/상욱추가)
        // ListActivity에서 setSupportActionBar를 쓰기 위해서 한것들... (170123/상욱추가)
        // ListActivity를 상속 받지 않으면 이것을 쓸 필요 없습니다
        AppCompatCallback callback = new AppCompatCallback() {
            @Override
            public void onSupportActionModeStarted(ActionMode mode) {
            }

            @Override
            public void onSupportActionModeFinished(ActionMode mode) {
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
                return null;
            }
        };
        delegate = AppCompatDelegate.create(this, callback);
        delegate.onCreate(savedInstanceState);
//        delegate.setContentView(R.layout.activity_main); // 이건 지워줘야 함... 메인액티비티에서 실행할테니...
    }

    // 네비게이션 드로어 재사용을 위해서... (170124/상욱추가)
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(fullView);

        // delegate 안지워도 되는것 같으니 남겨두세요 (170124/상욱추가)
        // 위에꺼는 안쓴다면 밑에 있는 delegate는 전부 지워주세요
        // 툴바 추가 (170123/상욱추가)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀에 있는 앱이름 숨기기

        // 네비게이션 추가(170123/상욱추가)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 단순히 액션바부터 네비게이션 돋보기 추가하기 위해서는 여기서 부터~~~
    // 뒤로가기 버튼 누를때... (170123/상욱추가)
    // 네비게이션 드로어가 켜져 있을때 뒤로가기 누르면 드로어가 꺼지고 드로어가 켜져 있지 않을때 뒤로가기 누르면 진짜로 뒤로 감 (170124/상욱추가)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 액션바 맨 오른쪽 돋보기 추가 (170123/상욱추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        delegate.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 돋보기 눌렀을때 효과... (170123/상욱추가)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 이곳에 돋보기 눌렀을때 검색액티비티로 이동할 코드 구현하시오!!!
        Toast.makeText(this, "검색 버튼 작동중", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    // 네비게이션 메뉴 선택시 일어날 작업을 입력하시오 (170123/상욱추가)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myPage) {
            // 이동할 액티비티를 작성하세요
        } else if (id == R.id.nav_bookmark) {

        } else if (id == R.id.nav_setup) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
