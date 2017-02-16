package com.ff.modealapplication.app.core.service;

import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.util.Base;
import com.ff.modealapplication.app.core.vo.ShopVo;
import com.ff.modealapplication.app.core.vo.daumvo.AddressVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by BIT on 2017-02-06.
 */

public class MapsService { // 단순한 서비스
    String url = Base.url + "modeal/map/";

    public List<ShopVo> fetchShopList(String range, String longitude, String latitude) {

//        String url = "http://163.44.171.41:8080/modeal/map/list?range="+range+"&longitude="+longitude+"&latitude="+latitude; // 제발 로컬에서 테스트 하지 말자...
//        String url = "http://192.168.1.93:8088/modeal/map/list?range="+range+"&longitude="+longitude+"&latitude="+latitude;
        HttpRequest httpRequest = HttpRequest.get(url + "list?range="+range+"&longitude="+longitude+"&latitude="+latitude);
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            //에러처리
            throw new RuntimeException("Http Response: " + responseCode);
        }

        JSONResultUserList jsonResultUserList = fromJSON(httpRequest, JSONResultUserList.class);

        return jsonResultUserList.getData();
    }

    public AddressVo fetchAddress(String addr) {
//        String url = "http://163.44.171.41:8080/modeal/map/addresstopoint";
//        String url = "http://192.168.1.93:8088/modeal/map/addresstopoint";
        HttpRequest httpRequest = HttpRequest.get(url + "addresstopoint");
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);
        int responseCode = httpRequest.send("addr=" + addr).code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            //에러처리
            throw new RuntimeException("Http Response:::: " + responseCode);
        }
        JSONResultAddress jsonResultAddress = fromJSON(httpRequest, JSONResultAddress.class);

        Log.d("jsonResultAddress--->", "" + jsonResultAddress);
        return jsonResultAddress.getData();
    }

    protected <V> V fromJSON(HttpRequest request, Class<V> target) {
        V v = null;

        try {
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);
            reader.close();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return v;
    }

    private class JSONResultUserList extends JSONResult<List<ShopVo>> {
    }

    private class JSONResultAddress extends JSONResult<AddressVo> {

    }
}
