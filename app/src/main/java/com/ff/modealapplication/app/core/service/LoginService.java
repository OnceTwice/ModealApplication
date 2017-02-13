package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.UserVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by BIT on 2017-01-31.
 */

public class LoginService {
        String url = "http://192.168.1.107:8088/modeal/userapp"; // 학원 로컬
//        String url = "http://192.168.0.17:8088/modeal/userapp/login"; // 집

    // 일반로그인 & 페이스북로그인 & 구글로그인
    public Map<String, Object> login(UserVo userVo) {
        HttpRequest httpRequest = HttpRequest.post(url +"/login");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        httpRequest.send(toJson(userVo));

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Http Response : " + responseCode);
        }
        Map<String, Object> resultUser = fromJson(httpRequest, Map.class);

        return resultUser;
    }

    // 소셜로그인 회원정보 저장
    public void SocialJoin(UserVo userVo) {
        HttpRequest httpRequest = HttpRequest.post(url + "/social");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        httpRequest.send(toJson(userVo));

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Http Response : " + responseCode);
        }
    }

    // 비밀번호 찾기
    public UserVo findPW(String email) {
        HttpRequest httpRequest = HttpRequest.post(url + "/findpw");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        httpRequest.send("email=" + email);

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Http Response : " + responseCode);
        }
        UserVo resultUser = fromJson(httpRequest, UserVo.class);

        return resultUser;
    }

    // 서버에서부터 UserVo로 보내고 있어서 이건 쓸 필요가 지금은 없다
    private class JSONResultUser extends JSONResult<UserVo> {
    }

    // JSON → 타입에 맞게 변환
    protected <V> V fromJson(HttpRequest httpRequest, Class<V> target) {
        V v = null;
        try {
            Gson gson = new GsonBuilder().create();

            Reader reader = httpRequest.bufferedReader();
            v = gson.fromJson(reader, target);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
