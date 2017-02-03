package com.ff.modealapplication.app.ui.login;

import android.util.Log;

import com.ff.modealapplication.app.core.vo.UserVo;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitService {

    public UserVo login(UserVo userVo /*final LoginCallback loginCallback*/) {
        Response response = null;
        try {
            response = new RetrofitLoginService().api().login(userVo).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response == null) {
                if (userVo.getManagerIdentified() == 3) {
                    return userVo;
                }
                userVo.setId("No ID");
                return userVo;
            }
            return (UserVo) response.body();
        }
    }

    public void FBJoin(UserVo userVo) {
        new RetrofitLoginService().api().fbjoin(userVo).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.w("성", "공"+response.body());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.w("망", "함"+t);
            }
        });
    }
//        new RetrofitLoginService().api().createUser(userVo).enqueue(new Callback<UserVo>() {
//            @Override
//            public void onResponse(Call<UserVo> call, Response<UserVo> response) {
//                Log.i("============", response.body() + "←여기값나옴");
//            }
//
//            @Override
//            public void onFailure(Call<UserVo> call, Throwable t) {
//            }
//        });
//    }
//                if (response != null && response.isSuccessful() && response.body() != null) {
//                    Log.w("!!!", response.body() + "????");
////                    loginCallback.onSuccess(response.body());
//                }
//                    Log.w("실패시...!!!", response.body() + "????");
//            }
//
//                loginCallback.onError();
//
//    public interface LoginCallback {
//        void onSuccess(UserVo userVo);
//        void onError();
//    }
}

