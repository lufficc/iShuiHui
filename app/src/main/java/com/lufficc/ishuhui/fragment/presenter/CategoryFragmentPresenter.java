package com.lufficc.ishuhui.fragment.presenter;

import android.support.annotation.NonNull;

import com.lufficc.ishuhui.fragment.IView.IView;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.ComicModel;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by lcc_luffy on 2016/2/1.
 */
public class CategoryFragmentPresenter {
    private IView<ComicModel> iView;
    private Call<ComicModel> call;

    public CategoryFragmentPresenter(@NonNull IView<ComicModel> iView) {
        this.iView = iView;
    }

    public void getData(String classifyId, int pageIndex) {
        call = RetrofitManager.api().getComic(classifyId, pageIndex);
        call.enqueue(new Callback<ComicModel>() {
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

    public void onDestroy() {
        iView = null;
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
}
