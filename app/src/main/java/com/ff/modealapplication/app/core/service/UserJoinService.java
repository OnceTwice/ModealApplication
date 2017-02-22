package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.ff.modealapplication.app.core.util.Base;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.domain.UserVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserJoinService {
    public List<UserVo> fetchUserList(String id, String password, String gender, String location, String birth) {
        String url = Base.url + "modeal/user/app/userinput";
        HttpRequest httpRequest = HttpRequest.get(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);     // 전달 타입
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);          // 받을 타입
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Log.d("=======================", "사용자 서비스 입갤");
        Log.d("서비스쪽 아이디 : ", id);
        Log.d("서비스쪽 비밀번호 : ", password);
        Log.d("서비스쪽 성별 : ", gender);
        Log.d("서비스쪽 거주지역 : ", location);
        Log.d("서비스쪽 생일 : ", birth);

        int responseCode = httpRequest.send("id="+id+ "&password="+password+ "&gender="+gender+"&location="+location + "&birth="+birth).code();     // 웹으로 데이터 전달

        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }

//        System.out.println(fromJSON(httpRequest, UserJoinService.JSONResultUserList.class).getClass().getName());
//        Log.d("에러뜬당",""+fromJSON(httpRequest, UserJoinService.JSONResultUserList.class).getClass().getName());

//        UserJoinService.JSONResultUserList jsonResult = fromJSON(httpRequest, UserJoinService.JSONResultUserList.class);            // 에러 뜸

        return null;
    }

    public void UserLeave(){                    //유저 탈퇴
        String url= Base.url + "modeal/user/app/userleave";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Long no = (Long) LoginPreference.getValue(getApplicationContext(),"no");
        Log.d("dfndflsdndlfdnf",""+no);
        int responseCode=httpRequest.send("no="+no).code();

        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP Response :" + responseCode);
        }
    }

//    public class JSONResultUserList extends JSONResult<List<UserVo>> {
//    }

    // JSON 문자열을 자바 객체로 변환
    protected <V> V fromJSON(HttpRequest request, Class<V> target ) {
        V v = null;

        try {
            Gson gson = new GsonBuilder().create();         // GSON 인스턴스 생성

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);              // JSONResultUserList 클래스 객체로 변환
            reader.close();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        return v;
    }
}
