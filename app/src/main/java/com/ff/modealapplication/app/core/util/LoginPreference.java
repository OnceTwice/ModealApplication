package com.ff.modealapplication.app.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ff.modealapplication.app.core.domain.UserVo;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LEE SANG WOOK on 2017-02-04.
 */

public class LoginPreference {

    public static void put(Context context, UserVo userVo) {            // 사용자일 때
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("no", userVo.getNo());
        editor.putString("id", userVo.getId());
        editor.putString("password", userVo.getPassword());
        editor.putString("gender", userVo.getGender());
        editor.putString("location", userVo.getLocation());
        editor.putString("birth", userVo.getBirth());
        editor.putLong("managerIdentified", userVo.getManagerIdentified());

        if (userVo.getShopNo() != null) {
            editor.putLong("shopNo", userVo.getShopNo());
        }

        editor.commit();
    }

    public static void put(Context context, Map<String, Object> resultUser) {   // 사업자일 때
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong("no", ((Double)resultUser.get("no")).longValue());
        editor.putString("id", (String)resultUser.get("id"));
        editor.putString("password", (String)resultUser.get("password"));
        editor.putString("gender", (String)resultUser.get("gender"));
        editor.putString("location", (String)resultUser.get("location"));
        editor.putString("birth", (String)resultUser.get("birth"));
        editor.putLong("managerIdentified", ((Double)resultUser.get("managerIdentified")).longValue());

        if (resultUser.get("shopNo") != null) {
            editor.putLong("shopNo", ((Double)resultUser.get("shopNo")).longValue());
            editor.putString("address", (String)resultUser.get("address"));
            editor.putString("newaddress", (String)resultUser.get("newaddress"));
            editor.putString("name", (String)resultUser.get("name"));
            editor.putString("phone", (String)resultUser.get("phone"));
            editor.putString("picture", (String)resultUser.get("picture"));
            editor.putString("introduce", (String)resultUser.get("introduce"));
        }

        editor.commit();
    }

    // long 타입도 getString으로 받아와서 에러가 떴었음...
    public static Object getValue(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        if (key.equals("managerIdentified") || key.equals("shopNo") || key.equals("no")) {
            return pref.getLong(key, -1);
        }
        return pref.getString(key, null);
    }

    public static void removeAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
