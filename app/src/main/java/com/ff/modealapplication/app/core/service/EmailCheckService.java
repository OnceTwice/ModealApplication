package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;

public class EmailCheckService {
    public Integer checkedEmail(String email) {
        String url = "http://192.168.1.26:8088/modeal/user/app/check";
        HttpRequest httpRequest = HttpRequest.get(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);     // 전달 타입
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);          // 받을 타입
        httpRequest.connectTimeout(7000);
        httpRequest.readTimeout(3000);

        Log.d("=======================", "사용자 서비스 입갤");

        int responseCode = httpRequest.send("email="+email).code();     // 웹으로 데이터 전달

        System.out.println("리스폰스코드 : " + responseCode);         // 200이면 정상

        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }

        return fromJSON(httpRequest, Integer.class);
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
