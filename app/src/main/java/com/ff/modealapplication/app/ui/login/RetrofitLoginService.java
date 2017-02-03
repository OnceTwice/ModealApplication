package com.ff.modealapplication.app.ui.login;

import android.text.TextUtils;

import com.ff.modealapplication.app.core.vo.UserVo;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by BIT on 2017-02-02.
 */

public class RetrofitLoginService {
    public ListAPI api() {
        return (ListAPI) retrofit(ListAPI.class);
    }

    public interface ListAPI {
        @POST("login")
        Call<UserVo> login(@Body UserVo userVo);

        @POST("fbjoin")
        Call<UserVo> fbjoin(@Body UserVo userVo);
    }

    public Object retrofit(Class<?> className) {
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create()).build().create(className);
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create(gson)).build();
        return new Retrofit.Builder().baseUrl("http://192.168.1.15:8088/modeal/userapp/").addConverterFactory(GsonConverterFactory.create()).client(client()).build().create(className);
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create(gson)).client(client()).build();
    }

    private OkHttpClient client() {
        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder().method(original.method(), original.body());

                String token = "sample-token";
                if (!TextUtils.isEmpty(token))
                    requestBuilder.header("Authorization", token);

                String locale = Locale.getDefault().getLanguage();
                requestBuilder.header("X-Locale", locale);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).build();
    }
}
