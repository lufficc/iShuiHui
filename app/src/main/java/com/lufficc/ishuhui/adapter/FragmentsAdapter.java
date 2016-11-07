package com.lufficc.ishuhui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lufficc.ishuhui.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lufficc on 2016/10/28.
 */

public class FragmentsAdapter extends FragmentPagerAdapter {
    protected final List<BaseFragment> fragments = new ArrayList<>(3);

    public FragmentsAdapter(FragmentManager fm, BaseFragment first, BaseFragment... fragments) {
        super(fm);
        this.fragments.add(first);
        if (fragments != null) {
            Collections.addAll(this.fragments, fragments);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
}
