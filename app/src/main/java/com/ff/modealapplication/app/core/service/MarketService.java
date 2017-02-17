package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.util.Base;
import com.github.kevinsawicki.http.HttpRequest;

import java.net.HttpURLConnection;
import java.util.Map;

public class MarketService {
    public Map<String, Object> marketInform(Long shopNo) {
        String url = Base.url + "modeal/market/detail";
        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        // 안드로이드에서 이클립스로 데이터 보내기
        int responseCode = httpRequest.send("no=" + shopNo).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP Response : " + responseCode);
        }

        // 이클립스에서 안드로이드로 데이터 받기
        MarketInform marketInform = Base.fromJSON(httpRequest, MarketInform.class);

        return marketInform.getData();
    }

    private class MarketInform extends JSONResult<Map<String, Object>> {
    }
}
