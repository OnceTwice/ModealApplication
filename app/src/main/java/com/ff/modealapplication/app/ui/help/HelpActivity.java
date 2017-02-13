package com.ff.modealapplication.app.ui.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.HelpService;
import com.ff.modealapplication.app.core.vo.HelpVo;

public class HelpActivity extends AppCompatActivity {

    private  HelpService helpService = new HelpService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // 자체 제작 액션바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표 표시

        helpService.init(this);
        //문의 버튼 클릭시 발송 & Toast 띄우기
       findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(HelpActivity.this, "문의사항이 등록되었습니다.",Toast.LENGTH_SHORT).show();
               new HelpasyncTask().execute();
               finish();
           }
       });


    }
   public class  HelpasyncTask extends SafeAsyncTask<HelpVo>{

       @Override
       public HelpVo call() throws Exception {


           helpService.HelpInsert();

           return null;
       }

       @Override
       protected void onSuccess(HelpVo helpVo) throws Exception {
//           super.onSuccess(helpVo);
       }

       @Override
       protected void onException(Exception e) throws RuntimeException {
           throw new RuntimeException("Help--->"+e);
       }
   }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) { // 뒤로가기 클릭시 searchActivity 종료
            this.finish();
            return true;
        } else if (id == R.id.action_button) {
        }

        return super.onOptionsItemSelected(item);
    }

}
