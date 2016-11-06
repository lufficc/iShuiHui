package com.lufficc.ishuhui.fragment.presenter;

import android.support.annotation.NonNull;

import com.lufficc.ishuhui.fragment.IView.IView;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.ComicsModel;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by lcc_luffy on 2016/2/1.
 */
public class SubscribeFragmentPresenter {
    private IView<ComicsModel> iView;
    private Call<ComicsModel> call;
    public SubscribeFragmentPresenter(@NonNull IView<ComicsModel> iView) {
        this.iView = iView;
    }

    public void getSubscribedComics() {
        call = RetrofitManager.api().getSubscribedComics();
        call.enqueue(new Callback<ComicsModel>() {
                    @Override
                    public void onResponse(retrofit2.Call<ComicsModel> call, retrofit2.Response<ComicsModel> response) {
                        if (response.isSuccessful()) {
                            iView.onSuccess(response.body());
                        } else {
                            iView.onFailure(new Exception(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ComicsModel> call, Throwable t) {
                        iView.onFailure(t);
                    }
                });
    }

    public void onDestroy() {
        iView = null;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
}
