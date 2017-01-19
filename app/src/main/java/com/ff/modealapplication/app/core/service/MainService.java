package com.ff.modealapplication.app.core.service;


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
    public List<ItemVo> MainList(){
        String url="http://192.168.1.14:8888/modeal/main/list";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(2000);
        httpRequest.readTimeout(2000);

        int responseCode=httpRequest.code();
        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP RESPONSE :" + responseCode);
        }

        JSONResultMainList jsonResultMainList = fromJSON(httpRequest, JSONResultMainList.class);
        return null;
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
