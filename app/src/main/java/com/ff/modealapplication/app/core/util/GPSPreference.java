package com.ff.modealapplication.app.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LEE SANG WOOK on 2017-02-11.
 */

public class GPSPreference {
    // 위도, 경도
    public static void put(Context context, String latitude, String longitude) {
        SharedPreferences pref = context.getSharedPreferences("gpsInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.commit();
    }

    // 반경
    public static void put(Context context, int range) {
        SharedPreferences pref = context.getSharedPreferences("gpsInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("range", range);
        editor.commit();
    }

    // long 타입도 getString으로 받아와서 에러가 떴었음...
    public static Object getValue(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("gpsInfo", MODE_PRIVATE);
        if (key.equals("range")) {
            return pref.getInt(key, 1000); // 초기 반경 안정했을 경우 1000m
        }
        return pref.getString(key, "noData");
    }

    public static void removeAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences("gpsInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
