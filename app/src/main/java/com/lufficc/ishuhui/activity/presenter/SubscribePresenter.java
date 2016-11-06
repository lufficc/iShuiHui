package com.lufficc.ishuhui.activity.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lufficc.ishuhui.activity.iview.SubscribeView;
import com.lufficc.ishuhui.data.source.comic.ComicsDataSource;
import com.lufficc.ishuhui.data.source.comic.ComicsRepository;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.utils.SubscribeUtil;

/**
 * Created by lufficc on 2016/8/28.
 */

public class SubscribePresenter {
    private SubscribeView subscribeView;

    public SubscribePresenter(@NonNull SubscribeView subscribeView) {
        this.subscribeView = subscribeView;
    }

    public void subscribe(final Comic comic, final boolean isSubscribed) {
        ComicsRepository.getInstance().subscribe(comic, !isSubscribed, new ComicsDataSource.SubscribeComicCallback() {
            @Override
            public void onComicSubscribe(boolean subscribe) {
                SubscribeUtil.subscribe(comic.Id, subscribe);
                subscribeView.onSubscribe(subscribe);
                Log.i("SubscribePresenter","subscribe:"+subscribe);

            }

            @Override
            public void onSubscribeFailed(Throwable throwable) {
                subscribeView.onFailSubscribe(throwable.getMessage());
            }
        });

    }

    public void onDestroy() {
        subscribeView = null;
    }
}
