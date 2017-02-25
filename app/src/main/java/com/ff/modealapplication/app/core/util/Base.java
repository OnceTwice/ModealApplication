package com.ff.modealapplication.app.core.util;

import com.ff.modealapplication.R;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.Reader;

public class Base {
    public static final String url = "http://192.168.1.26:8088/";       // 자신의 ip주소

    public static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile ) // resource or drawable
            .showImageForEmptyUri( R.drawable.apple )  // resource or drawable
            .showImageOnFail( R.drawable.apple )       // resource or drawable
            //.resetViewBeforeLoading( false )  // default
            .delayBeforeLoading( 0 )
            //.cacheInMemory( false )   // default
            .cacheOnDisc( true )        // false is default
            //.preProcessor(...)
            //.postProcessor(...)
            //.extraForDownloader(...)
            //.considerExifParams( false )  // default
            //.imageScaleType( ImageScaleType.IN_SAMPLE_POWER_OF_2 )// default
            //.bitmapConfig( Bitmap.Config.ARGB_8888 )  // default
            //.decodingOptions(...)
            //.displayer( new SimpleBitmapDisplayer() ) // default
            //.handler( new Handler() )     // default
            .build();

    public static <V> V fromJSON(HttpRequest request, Class<V> target ) {        // JSON 문자열을 자바 객체로 변환
        V v = null;

        try {
            Gson gson = new GsonBuilder().create();         // GSON 인스턴스 생성

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);              // JSONResultUserList 클래스 객체로 변환
            reader.close();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        return v;
    }

    // 타입에 해당하는 OBJECT → JSON
    public static String toJson(Object o) {

        String json = "";

        try {
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    // 윤년조건 : 4로 나눈 나머지가 0인 해로서 100의 배수인 해는 제외하며, 400의 배수인 해는 포함
    public static int LeafYear(int year) {                      // 윤년:2월29일, 평년:2월28일
        int day = 0;
        boolean isYun = false;

        if ((0 == (year % 4) && 0 != (year % 100)) || 0 == year % 400) {
            isYun = true;
        } else {
            isYun = false;
        }

        if (isYun) {        // 윤년이면 그 달은 29일까지 존재
            day = 29;
        } else {            // 평년이면 그 달은 28일까지 존재
            day = 28;
        }

        return day;
    }

    public static int returnDay(int month) {
        int day = 0;

        if(month==1 || month== 3 || month==5 || month==7 || month==8 || month==10 || month== 12) {
            day = 31;
        } else if(month==4 || month==6 || month==9 || month==11) {
            day = 30;
        }

        return day;
    }
}
