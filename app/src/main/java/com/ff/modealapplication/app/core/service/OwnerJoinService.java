package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.ShopVo;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

public class OwnerJoinService {
    public UserVo fetchOwnerList(UserVo userVo, ShopVo shopVo) {
        String url = "http://192.168.1.26:8088/modeal/user/app/ownerinput";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);     // 전달 타입
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);          // 받을 타입
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Log.d("=======================", "사업자 서비스 입갤");
        System.out.println("USERVO=====" + userVo);
        System.out.println("SHOPVO=====" + shopVo);

        httpRequest.send(toJson(userVo));

        System.out.println(userVo);
        System.out.println(toJson(userVo));

        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Http Response : " + responseCode);
        }

        JSONResultOwner jsonResultOwner = fromJSON(httpRequest, JSONResultOwner.class);

        return (UserVo) jsonResultOwner.getData();
    }

    public class JSONResultOwner extends JSONResult<List<UserVo>> {
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

    // 타입에 해당하는 OBJECT → JSON
    protected String toJson(Object o) {

        String json = "";

        try {
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return json;
    }
}
