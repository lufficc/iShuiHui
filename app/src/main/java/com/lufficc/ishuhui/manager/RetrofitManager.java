package com.lufficc.ishuhui.manager;


import com.lufficc.ishuhui.api.Api;
import com.lufficc.ishuhui.model.User;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lcc_luffy on 2016/8/8.
 */

public class RetrofitManager {
    private Retrofit retrofit;

    private Api api;

    private static final class Holder {
        private static final RetrofitManager RETROFIT_MANAGER = new RetrofitManager();
    }

    private RetrofitManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new UserInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ishuhui.net/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Api api() {
        return Holder.RETROFIT_MANAGER.api == null ? Holder.RETROFIT_MANAGER.api = get().create(Api.class) : Holder.RETROFIT_MANAGER.api;
    }

    private class UserInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            if (User.getInstance().isLogin()) {
                Request newRequest = oldRequest.newBuilder()
                        .addHeader(User.COOKIE_KEY, User.getInstance().getCookie())
                        .build();
                return chain.proceed(newRequest);
            }
            return chain.proceed(oldRequest);
        }
    }

    public static Retrofit get() {
        return Holder.RETROFIT_MANAGER.retrofit;
    }
}
