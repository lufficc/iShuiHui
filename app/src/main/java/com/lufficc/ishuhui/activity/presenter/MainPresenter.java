package com.lufficc.ishuhui.activity.presenter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.MainView;
import com.lufficc.ishuhui.fragment.ViewPagerFragment;
import com.lufficc.ishuhui.manager.FirManager;
import com.lufficc.ishuhui.model.FirLatestModel;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lufficc.ishuhui.utils.ScreenUtil.context;

/**
 * Created by lufficc on 2016/8/26.
 */

public class MainPresenter {
    private final MainView mainView;
    private final FragmentManager fragmentManager;
    private SparseArray<Fragment> fragmentMap = new SparseArray<>(4);
    private Fragment currentFragment;

    public MainPresenter(@NonNull MainView mainView, @NonNull FragmentManager fragmentManager) {
        this.mainView = mainView;
        this.fragmentManager = fragmentManager;
        switchFragment(get());
        checkUpdate();
    }

    public void setCurrentFragment(int index) {
        getViewPagerFragment().setCurrentFragment(index);
    }

    public void checkUpdate() {
        FirManager.fir().latest().enqueue(new Callback<FirLatestModel>() {
            @Override
            public void onResponse(Call<FirLatestModel> call, Response<FirLatestModel> response) {
                if (response.isSuccessful()) {
                    PackageManager pm = context.getPackageManager();//context为当前Activity上下文
                    PackageInfo pi = null;
                    try {
                        pi = pm.getPackageInfo(context.getPackageName(), 0);
                        FirLatestModel firLatestModel = response.body();
                        if (pi.versionCode < firLatestModel.build) {
                            mainView.onUpdate(firLatestModel);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<FirLatestModel> call, Throwable t) {

            }
        });
    }

    public int getMenuId() {
        switch (get()) {
            default:
                return 1;
        }
    }

    private String[] string2Array(String json) {
        JsonArray array = JsonUtil.fromJson(json, JsonArray.class);
        String[] strings = new String[array.size()];
        for (int i = array.size() - 1; i >= 0; i--) {
            strings[i] = array.get(i).getAsString();
        }
        return strings;
    }

    private void save(int fragmentId) {
        PtrUtil.getInstance().start().put("default_fragment", fragmentId).commit();
    }

    private int get() {
        int defaultFragment = MainView.FRAGMENT_VIEW_PAGER;
        return PtrUtil.getInstance().getInt("default_fragment", defaultFragment);
    }

    private ViewPagerFragment viewPagerFragment;

    private ViewPagerFragment getViewPagerFragment() {
        return viewPagerFragment == null ? viewPagerFragment = ViewPagerFragment.newInstance() : viewPagerFragment;
    }

    public void switchFragment(int fragmentId) {
        save(fragmentId);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentMap.get(fragmentId);
        if (fragment != null && fragment == currentFragment)
            return;
        if (fragment == null) {
            switch (fragmentId) {
                case MainView.FRAGMENT_SEARCH:
                    break;
                default:
                    fragment = getViewPagerFragment();
                    break;
            }
            fragmentMap.put(fragmentId, fragment);
        }
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (currentFragment == null) {
            transaction.add(R.id.container, fragment, String.valueOf(fragmentId));
        } else {
            if (fragment.isAdded()) {
                transaction
                        .hide(currentFragment)
                        .show(fragment);
            } else {
                transaction
                        .hide(currentFragment)
                        .add(R.id.container, fragment, fragment.toString());
            }

        }
        transaction.commit();
        mainView.onShowFragment(fragment);
        currentFragment = fragment;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
