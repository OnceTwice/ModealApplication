package com.ff.modealapplication.app.ui.login;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by BIT on 2017-02-02.
 */

public class RetrofitService {
    protected static Object retrofit(Class<?> className) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.1.15:8088/modeal/userapp/login").addConverterFactory(GsonConverterFactory.create()).build();
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create(gson)).build();
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create()).client(client()).build();
//        return new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create(gson)).client(client()).build();
        return retrofit.create(className);
    }
//  private static OkHttpClient client()
//  {
//    return new OkHttpClient.Builder().addInterceptor(new Interceptor()
//    {
//      @Override
//      public Response intercept(Chain chain) throws IOException
//      {
//        Request original = chain.request();
//        Request.Builder requestBuilder = original.newBuilder().method(original.method(), original.body());
//
//        String token = "sample-token";
//        if (!TextUtils.isEmpty(token))
//          requestBuilder.header("Authorization", token);
//
//        String locale = Locale.getDefault().getLanguage();
//        requestBuilder.header("X-Locale", locale);
//
//        Request request = requestBuilder.build();
//        return chain.proceed(request);
//      }
//    }).build();
//  }
}

