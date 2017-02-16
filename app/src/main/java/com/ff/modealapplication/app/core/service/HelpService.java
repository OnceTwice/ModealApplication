package com.ff.modealapplication.app.core.service;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.JSONResult;
import com.ff.modealapplication.app.core.util.Base;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.ff.modealapplication.app.core.domain.HelpVo;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by BIT on 2017-01-24.
 */

public class HelpService {
    private static Context mHelp;

    public static void init(Context help){
        mHelp=help;
    }


    public void HelpInsert(){
        String url= Base.url+"modeal/helpapp/insert";

        HttpRequest httpRequest = HttpRequest.post(url);

        httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.accept(HttpRequest.CONTENT_TYPE_JSON);
        httpRequest.connectTimeout(3000);
        httpRequest.readTimeout(3000);

        httpRequest.send(toJSON());
        int responseCode=httpRequest.code();

        if(responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("HTTP Response :" + responseCode);
        }
    }

    public class JSONSearhList extends JSONResult<HelpVo>{
    }



    protected String toJSON(){
        String json="";
        try{
            Gson gson = new GsonBuilder().create();

            String title=((EditText)((Activity)mHelp).findViewById(R.id.help_title_text)).getText().toString();
            String complain=((EditText)((Activity)mHelp).findViewById(R.id.help_content_text)).getText().toString();
            Long usersNo = (Long) LoginPreference.getValue(getApplicationContext(),"no");

            HelpVo helpVo = new HelpVo();
            helpVo.setTitle(title);
            helpVo.setComplain(complain);
            helpVo.setUserNo(usersNo);


            json= gson.toJson(helpVo);
        } catch ( Exception e){
            throw  new RuntimeException(e);
        }
        return  json;
    }

}

