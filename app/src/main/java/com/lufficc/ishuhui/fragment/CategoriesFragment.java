package com.lufficc.ishuhui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.CategoriesFragmentsAdapter;

import butterknife.BindView;

public class CategoriesFragment extends BaseFragment {
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private static final String CURRENT_FRAGMENT = "categories.current.fragment";

    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        viewPager.setAdapter(new CategoriesFragmentsAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        int current = 0;
        if (savedInstanceState != null) {
            current = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);
        }
        viewPager.setCurrentItem(current);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_FRAGMENT, viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public String toString() {
        return "搜索";
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_categoties;
    }
}
