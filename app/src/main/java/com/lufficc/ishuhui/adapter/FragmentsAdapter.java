package com.lufficc.ishuhui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lufficc on 2016/10/28.
 */

public class FragmentsAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>(3);

    public FragmentsAdapter(FragmentManager fm, Fragment first, Fragment... fragments) {
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
    public int getCount() {
        return fragments.size();
    }
}
