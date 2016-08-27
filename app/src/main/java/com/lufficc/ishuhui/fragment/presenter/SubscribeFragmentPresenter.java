package com.lufficc.ishuhui.fragment.presenter;

import android.support.annotation.NonNull;

import com.lufficc.ishuhui.fragment.IView.IView;
import com.lufficc.ishuhui.model.ComicModel;
import com.lufficc.ishuhui.manager.RetrofitManager;

import retrofit2.Callback;

/**
 * Created by lcc_luffy on 2016/2/1.
 */
public class SubscribeFragmentPresenter {
    private IView<ComicModel> iView;

    public SubscribeFragmentPresenter(@NonNull IView<ComicModel> iView) {
        this.iView = iView;
    }

    public void getSubscribedComics() {
        RetrofitManager.api()
                .getSubscribedComics()
                .enqueue(new Callback<ComicModel>() {
                    @Override
                    public void onResponse(retrofit2.Call<ComicModel> call, retrofit2.Response<ComicModel> response) {
                        if (response.isSuccessful()) {
                            iView.onSuccess(response.body());
                        } else {
                            iView.onFailure(call, new Exception(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ComicModel> call, Throwable t) {
                        iView.onFailure(call, t);
                    }
                });
    }
}
