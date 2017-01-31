package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

public class UserJoinService {
    public List<UserVo> fetchUserList(String id, String password, String gender, String location, String birth) {
        String url = "http://192.168.1.26:8088/modeal/user/app/input";
        HttpRequest httpRequest = HttpRequest.get(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);     // 전달 타입
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);          // 받을 타입
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Log.d("아이디 : ", id);
        Log.d("비밀번호 : ", password);
        Log.d("성별 ", gender);
        Log.d("위치", location);
        Log.d("생년월일 : ", birth);

        int responseCode = httpRequest.code();

        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }

        UserJoinService.JSONResultUserList jsonResult = fromJSON(httpRequest, UserJoinService.JSONResultUserList.class);

        return jsonResult.getData();
    }

    public class JSONResultUserList extends JSONResult<List<UserVo>> {
    }

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
