package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.domain.NoticeVo;
import com.ff.modealapplication.app.core.util.Base;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by BIT on 2017-02-15.
 */

public class NoticeService {

    public List<NoticeVo> getNoticeList(){
        String url= Base.url + "modeal/noticeapp";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        int responseCode=httpRequest.code();
        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP Response :"+responseCode);
        }

        JSONNoticeList jsonNoticeList = fromJSON(httpRequest, JSONNoticeList.class);


        return jsonNoticeList.getData();
    }

    public class JSONNoticeList extends JSONResult<List<NoticeVo>>{}

    public <V> V fromJSON(HttpRequest request, Class<V> target){
        V v = null;
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
