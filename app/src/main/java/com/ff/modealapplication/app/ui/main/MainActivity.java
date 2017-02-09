package com.ff.modealapplication.app.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.ui.help.HelpActivity;
import com.ff.modealapplication.app.ui.item.ItemActivity;
import com.ff.modealapplication.app.ui.join.JoinActivity;
import com.ff.modealapplication.app.ui.login.LoginActivity;
import com.ff.modealapplication.app.ui.map.MainMapFragment;
import com.ff.modealapplication.app.ui.map.SearchMapRangeActivity;
import com.ff.modealapplication.app.ui.market.MarketDetailInformationActivity;
import com.ff.modealapplication.app.ui.message.MessagingActivity;
import com.ff.modealapplication.app.ui.mypage.OwnerMyPageActivity;
import com.ff.modealapplication.app.ui.mypage.UserMyPageActivity;
import com.ff.modealapplication.app.ui.search.SearchActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, MainMapFragment.OnFragmentInteractionListener {
    private Fragment mainListFragment;

    private DrawerLayout drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class)); // 스플래시 화면 (170131/상욱추가)

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListFragment = new MainListFragment();

        // 프래그먼트
        // 프래그먼트의 commit은 여러번 하면 에러가 뜨므로... commit이 필요할때마다 프래그먼트트랜잭션을 만들어서 사용한다
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_content, mainListFragment).commit();

        // 툴바 추가 (170123/상욱추가)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀에 있는 앱이름 숨기기

        // 네비게이션 추가(170123/상욱추가)
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle); // setDrawerListener가 addDrawerListener로 바뀜 (170131/준현수정)
        toggle.syncState();

        // 로그인 클릭시
        ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button).setOnClickListener(this);

        // 회원가입 클릭시
        ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.register_button).setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LoginPreference.getValue(getApplicationContext(), "id") != null) {
            ((Button) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button)).setText("로그아웃");
        } else if (LoginPreference.getValue(getApplicationContext(), "id") == null) {
            ((Button) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button)).setText("로그인");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LoginPreference.getValue(getApplicationContext(), "id") != null) {
            ((Button) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button)).setText("로그아웃");
        } else if (LoginPreference.getValue(getApplicationContext(), "id") == null) {
            ((Button) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.login_button)).setText("로그인");
        }
    }


    // 단순히 액션바부터 네비게이션 돋보기 추가하기 위해서는 여기서 부터~~~
    // 뒤로가기 버튼 누를때... (170123/상욱추가)
    // 네비게이션 드로어가 켜져 있을때 뒤로가기 누르면 드로어가 꺼지고 드로어가 켜져 있지 않을때 뒤로가기 누르면 진짜로 뒤로 감 (170124/상욱추가)
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                if (((Button) findViewById(R.id.login_button)).getText() == "로그인") {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                } else if (((Button) findViewById(R.id.login_button)).getText() == "로그아웃") {
                    Toast.makeText(this, LoginPreference.getValue(getApplicationContext(), "id") + " 로그아웃", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    LoginPreference.removeAll(getApplicationContext());
                    onPause();
                    onResume();
                    break;
                }
            case R.id.register_button:
                Intent intent1 = new Intent(this, JoinActivity.class);
                startActivity(intent1);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    // 액션바 맨 오른쪽 돋보기 추가 (170123/상욱추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.include_map, menu);
        return true;
    }

    // 돋보기 눌렀을때 효과... (170123/상욱추가)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 이곳에 돋보기 눌렀을때 검색액티비티로 이동할 코드 구현하시오!!!
        if (item.getItemId() == R.id.action_button) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.map_button) {
            Intent intent = new Intent(this, SearchMapRangeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    // 네비게이션 메뉴 선택시 일어날 작업을 입력하시오 (170123/상욱추가)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_myPage) {
            if ((Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == -1) {       // 로그인 안했을때
                Toast.makeText(this, "로그인하세요", Toast.LENGTH_SHORT).show();
            } else if ((Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == 1) { // 사용자 로그인 했을때
                Toast.makeText(this, "사용자 페이지", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, UserMyPageActivity.class);
                startActivity(intent);
            } else if ((Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == 2) { // 사업자 로그인 했을때
                Toast.makeText(this, "사업자 페이지", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, OwnerMyPageActivity.class);
                startActivity(intent);
            } else if ((Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == 3 || (Long)LoginPreference.getValue(getApplicationContext(), "managerIdentified") == 4) { // 페이지북(3), 구글(4) 로그인 했을때
                Toast.makeText(this, "소셜로그인중...", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_bookmark) {

        } else if (id == R.id.nav_setup) {
            Intent intent = new Intent(this, MessagingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, ItemActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_marketDetail) {
            Intent intent = new Intent(this, MarketDetailInformationActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
