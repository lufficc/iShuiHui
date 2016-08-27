package com.lufficc.ishuhui.api;

import com.lufficc.ishuhui.Config;
import com.lufficc.ishuhui.model.FirLatestModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by lufficc on 2016/8/26.
 */

public interface Fir {
    @GET("/apps/latest/"+ Config.FIR_ID)
    Call<FirLatestModel> latest();
}
