package com.ff.modealapplication.app.core.service;


import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.ItemVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainService {
    public List<ItemVo> MainItemList(){
        String url="http://192.168.1.14:8888/modeal/mainapp/list";
        Log.d("dddd","fall");
        HttpRequest httpRequest = HttpRequest.get(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Log.d("---",""+httpRequest.code());
        int responseCode=httpRequest.code();
        Log.d("dddd","fall3");
        Log.d("------",""+responseCode);
        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }

        JSONResultMainList jsonResultMainList = fromJSON(httpRequest, JSONResultMainList.class);
        Log.d("whekr","dfdfd");
        return jsonResultMainList.getData();
    }

    private class JSONResultMainList extends JSONResult<List<ItemVo>>{
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
