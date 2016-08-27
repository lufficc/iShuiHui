package com.lufficc.ishuhui.activity.iview;

import android.support.v4.app.Fragment;

import com.lufficc.ishuhui.model.FirLatestModel;

/**
 * Created by lufficc on 2016/8/26.
 */

public interface MainView {
    int FRAGMENT_SUBSCRIBE = 5;
    int FRAGMENT_HOT = 1;
    int FRAGMENT_SHUHUI = 2;
    int FRAGMENT_SAME = 12;

    void onSuggestions(String[] strings);
    void onShowFragment(Fragment fragment);
    void onUpdate(FirLatestModel firLatestModel);
}
