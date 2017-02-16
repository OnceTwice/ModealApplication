package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.util.Base;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by BIT on 2017-02-14.
 */

public class BookmarkService {

    private String url = Base.url + "modeal/bookmarkapp/";

    // 즐겨찾기 추가
    public void bookmarkAdd(Long itemNo, Long userNo, Long shopNo) {
        HttpRequest httpRequest = HttpRequest.post(url + "addbookmark");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(5000);
        httpRequest.readTimeout(5000);

        if (shopNo == null) {
            httpRequest.send("itemNo=" + itemNo + "&userNo=" + userNo);
        } else if (itemNo == null) {
            httpRequest.send("shopNo=" + shopNo + "&userNo=" + userNo);
        }

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }
    }

    // 즐겨찾기 삭제
    public void bookmarkDelete(Long itemNo, Long userNo, Long shopNo) {
        HttpRequest httpRequest = HttpRequest.post(url + "deletebookmark");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(5000);
        httpRequest.readTimeout(5000);

        if (shopNo == null) {
            httpRequest.send("itemNo=" + itemNo + "&userNo=" + userNo);
        } else if (itemNo == null) {
            httpRequest.send("shopNo=" + shopNo + "&userNo=" + userNo);
        }

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }
    }

    // 즐겨찾기 검색
    public Long bookmarkSelect(Long itemNo, Long userNo, Long shopNo) {
        HttpRequest httpRequest = HttpRequest.post(url + "selectbookmark");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(5000);
        httpRequest.readTimeout(5000);

        if (shopNo == null) {
            httpRequest.send("itemNo=" + itemNo + "&userNo=" + userNo);
        } else if (itemNo == null) {
            httpRequest.send("shopNo=" + shopNo + "&userNo=" + userNo);
        }

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }

        return fromJSON(httpRequest, Long.class);
    }

    // 즐겨찾기 리스트
    public List<Map<String, Object>> bookmarkList(Long userNo) {
        HttpRequest httpRequest = HttpRequest.post(url + "listbookmark");

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(5000);
        httpRequest.readTimeout(5000);

        httpRequest.send("userNo=" + userNo);

        int responseCode = httpRequest.code();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }

        return fromJSON(httpRequest, JSONResultBookmarkList.class).getData();
    }

    private class JSONResultBookmarkList extends JSONResult<List<Map<String, Object>>> {
    }

    protected <V> V fromJSON(HttpRequest request, Class<V> target) {
        V v = null;
        try {
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);

            reader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return v;
    }
}
