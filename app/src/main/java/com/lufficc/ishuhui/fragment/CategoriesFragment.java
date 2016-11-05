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

    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        viewPager.setAdapter(new CategoriesFragmentsAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
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
