package com.ff.modealapplication.app.core.service;


import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainService {
    public List<Map<String, Object>> MainItemList(){
        String url="http://192.168.1.14:8888/modeal/mainapp/list";

        HttpRequest httpRequest = HttpRequest.get(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(5000);
        httpRequest.readTimeout(5000);

        int responseCode=httpRequest.code();
        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }
        Log.d("=====",""+responseCode);

        JSONResultMainList jsonResultMainList = fromJSON(httpRequest, JSONResultMainList.class);

        return jsonResultMainList.getData();
    }

    private class JSONResultMainList extends JSONResult<List<Map<String, Object>>>{
    }
    protected <V> V fromJSON(HttpRequest request, Class<V> target){
        V v = null;
        try{
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);

            reader.close();
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
        return v;
    }
}
