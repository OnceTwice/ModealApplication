package com.ff.modealapplication.app.core.service;

import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.vo.HelpVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;

/**
 * Created by BIT on 2017-01-24.
 */

public class HelpService {

    public void HelpInsert(String title,String complain,Long userNo){
        String url="http://192.168.0.103:8088/modeal/helpapp/insert";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        int responseCode=httpRequest.send(
                "title="+title +
                        "&complain="+complain+
                        "&userNo="+userNo
        ).code();

        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP Response :" + responseCode);
        }
    }

    public class JSONSearhList extends JSONResult<HelpVo>{
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

