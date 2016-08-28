package com.lufficc.ishuhui.activity.iview;

/**
 * Created by lufficc on 2016/8/28.
 */

public interface SubscribeView {
    void onSubscribe(boolean isSubscribed);
    void onFailSubscribe(String msg);
}
