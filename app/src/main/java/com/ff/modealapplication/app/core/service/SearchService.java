package com.ff.modealapplication.app.core.service;

import android.content.Context;
import android.util.Log;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.domain.ItemVo;
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
 * Created by BIT on 2017-01-24.
 */

public class SearchService {
    private static Context mSearch;

    public static void init(Context search){
        mSearch=search;
    }

    public List<ItemVo> searchList(){
        String url= Base.url+"modeal/list/search";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        int responseCode=httpRequest.code();
        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP Response :" + responseCode);
        }

        JSONSearhList jsonSearchList = fromJSON(httpRequest, JSONSearhList.class);

        return jsonSearchList.getData();
    }

    public List<Map<String, Object>> resultList(String latitude, String longitude, String name ){
        String url = Base.url+"modeal/list/resultsearch";

        HttpRequest httpRequest = HttpRequest.post(url);

        Log.d("aasdfasdf",latitude+"-"+longitude);
        httpRequest.contentType(HttpRequest.CONTENT_TYPE_FORM);
        httpRequest.accept(httpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(10000);
        httpRequest.readTimeout(10000);

        int responseCode=httpRequest.send(
                "name="+name +
                        "&latitude=" + latitude +
                        "&longitude=" + longitude
        ).code();

        if(responseCode != HttpURLConnection.HTTP_OK){
            throw  new RuntimeException("HTTP RESPNOSE :" +responseCode );
        }

        JSONSearchResultList jsonSearchResultList= fromJSON(httpRequest, JSONSearchResultList.class);

        return jsonSearchResultList.getData();
    }

    public class JSONSearchResultList extends JSONResult<List<Map<String, Object>>>{
    }

    public class JSONSearhList extends JSONResult<List<ItemVo>>{
    }


    protected <V> V fromJSON(HttpRequest request, Class<V> target){
        V v= null;
        try{
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v=gson.fromJson(reader, target);

            reader.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return v;
    }



}

