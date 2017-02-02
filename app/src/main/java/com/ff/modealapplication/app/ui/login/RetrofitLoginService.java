package com.ff.modealapplication.app.ui.login;

import com.ff.modealapplication.app.core.vo.UserVo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by BIT on 2017-02-02.
 */

public class RetrofitLoginService extends RetrofitService {
    public static ListAPI apt() {
        return (ListAPI)retrofit(ListAPI.class);
    }

    public interface ListAPI {
        @POST("users/new")
        Call<UserVo> createUser(@Body UserVo userVo);
    }
}
