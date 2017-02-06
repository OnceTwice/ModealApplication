package com.ff.modealapplication.app.ui.message;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by BIT on 2017-02-06.
 * 사용자 기기별 token을 생성하는 클래스 입니다.
 * 나중에 push 알림을 특정 타겟에게 보낼 때 사용되는 고유 키 값 입니다.
 * 이 토큰값을 용도에 맞게 사용하시면 됩니다.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
    }
}
