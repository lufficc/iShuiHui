package com.lufficc.ishuhui.adapter;

import android.support.v4.app.FragmentManager;

import com.lufficc.ishuhui.fragment.CategoryFragment;

/**
 * Created by lufficc on 2016/10/28.
 */

public class CategoriesFragmentsAdapter extends FragmentsAdapter {

    public CategoriesFragmentsAdapter(FragmentManager fm) {
        super(fm
                , CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_HOT)
                , CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_MOUSE)
                , CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_SAME));
    }
}
