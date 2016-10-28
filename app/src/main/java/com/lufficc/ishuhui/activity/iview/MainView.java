package com.lufficc.ishuhui.activity.iview;

import android.support.v4.app.Fragment;

import com.lufficc.ishuhui.model.FirLatestModel;

/**
 * Created by lufficc on 2016/8/26.
 */

public interface MainView {
    int FRAGMENT_VIEW_PAGER = 1;
    int FRAGMENT_SEARCH = 2;

    void onShowFragment(Fragment fragment);
    void onUpdate(FirLatestModel firLatestModel);
}
