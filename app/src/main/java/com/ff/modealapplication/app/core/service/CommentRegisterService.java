package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.domain.CommentVo;
import com.ff.modealapplication.app.core.util.Base;
import com.github.kevinsawicki.http.HttpRequest;

import java.net.HttpURLConnection;
import java.util.List;

public class CommentRegisterService {
    public List<CommentVo> commentRegister(CommentVo commentVo) {
        String url = Base.url + "modeal/commentapp/add";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);     // 전달 타입
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);          // 받을 타입
        httpRequest.connectTimeout(15000);
        httpRequest.readTimeout(3000);

        // 안드로이드에서 이클립스로 데이터 보내기
        httpRequest.send(Base.toJson(commentVo));

        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Http Response : " + responseCode);
        }

        // 이클립스에서 안드로이드로 데이터 받기
        CommentRegisterInformation commentRegisterInformation = Base.fromJSON(httpRequest, CommentRegisterInformation.class);

        return commentRegisterInformation.getData();
    }

    public class CommentRegisterInformation extends JSONResult<List<CommentVo>> {
    }
}
