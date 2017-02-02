package com.ff.modealapplication.app.core.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.ItemVo;
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
        String url="http://192.168.1.34:8888/modeal/list/search";

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

    public List<Map<String, Object>> resultList(){
        String url = "http://192.168.1.34:8888/modeal/list/resultsearch";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        Log.w("!!!!", toJSON());

        httpRequest.send(toJSON());
        int responseCode=httpRequest.code();
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

    protected String toJSON(){
        String json="";
        try{
            Gson gson = new GsonBuilder().create();

            String searchKeyword = ((TextView)((Activity) mSearch).findViewById(R.id.search_result)).getText().toString();

            ItemVo itemVo = new ItemVo();
            itemVo.setName(searchKeyword);

            json= gson.toJson(itemVo);
        } catch ( Exception e){
            throw  new RuntimeException(e);
        }
        return  json;
    }

}

