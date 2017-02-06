package com.ff.modealapplication.app.ui.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by BIT on 2017-02-06.
 */

public class MessagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        FirebaseMessaging.getInstance().subscribeToTopic("notice"); // 푸시 알림 전송시 같은 토픽명 그룹 전체에 메세지 전송 가능
        FirebaseInstanceId.getInstance().getToken(); // 사용자 기기의 고유 토큰 값

        Toast.makeText(this, "---" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_SHORT);
    }
}
