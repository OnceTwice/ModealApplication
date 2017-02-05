package com.ff.modealapplication.app.ui.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.ff.modealapplication.app.core.vo.UserVo;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LEE SANG WOOK on 2017-02-04.
 */

public class LoginPreference {

    public static void put(Context context, UserVo userVo) {
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("id", userVo.getId());
        editor.putString("password", userVo.getPassword());
        editor.putString("gender", userVo.getGender());
        editor.putString("location", userVo.getLocation());
        editor.putString("birth", userVo.getBirth());
        editor.putInt("managerIdentified", userVo.getManagerIdentified());
        if (userVo.getShopNo() != null) {
            editor.putLong("shopNo", userVo.getShopNo());
        }
        editor.commit();
    }

    public static String getValue(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public static void removeAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
