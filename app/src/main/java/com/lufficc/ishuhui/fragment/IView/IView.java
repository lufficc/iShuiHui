package com.lufficc.ishuhui.fragment.IView;


import retrofit2.Call;

/**
 * Created by lcc_luffy on 2016/2/1.
 */
public interface IView<Result> {
    void onSuccess(Result result);

    void onFailure(Throwable e);
}
