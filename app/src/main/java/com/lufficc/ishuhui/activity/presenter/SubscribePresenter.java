package com.lufficc.ishuhui.activity.presenter;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.lufficc.ishuhui.activity.iview.SubscribeView;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.utils.PtrUtil;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by lufficc on 2016/8/28.
 */

public class SubscribePresenter {
    private final SubscribeView subscribeView;
    private Call<JsonObject> subscribeCall;

    public SubscribePresenter(@NonNull SubscribeView subscribeView) {
        this.subscribeView = subscribeView;
    }

    public void subscribe(final int bookId, final boolean isSubscribed) {
        subscribeCall = RetrofitManager.api()
                .subscribe(String.valueOf(bookId), String.valueOf(!isSubscribed), 2);
        subscribeCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    subscribeView.onSubscribe(isSubscribed);
                    PtrUtil.getInstance()
                            .start()
                            .put("bookId" + bookId + "isSubscribed", isSubscribed)
                            .commit();
                } else {
                    subscribeView.onFailSubscribe(response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                subscribeView.onFailSubscribe(t.getMessage());
            }
        });
    }

    public void onDestroy() {
        if (subscribeCall != null && !subscribeCall.isCanceled())
            subscribeCall.cancel();
    }
}
