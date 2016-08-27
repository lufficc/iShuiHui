package com.lufficc.ishuhui.activity.presenter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.MainView;
import com.lufficc.ishuhui.fragment.CategoryFragment;
import com.lufficc.ishuhui.fragment.SubscribeFragment;
import com.lufficc.ishuhui.manager.FirManager;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.FirLatestModel;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lufficc.ishuhui.activity.iview.MainView.FRAGMENT_HOT;
import static com.lufficc.ishuhui.activity.iview.MainView.FRAGMENT_SAME;
import static com.lufficc.ishuhui.activity.iview.MainView.FRAGMENT_SHUHUI;
import static com.lufficc.ishuhui.activity.iview.MainView.FRAGMENT_SUBSCRIBE;
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
        getSuggestions();
        checkUpdate();
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
                        if (pi.versionCode == firLatestModel.build) {
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


    private void getSuggestions() {
        String hotWord = PtrUtil.getInstance().getString("hotWord", null);
        if (hotWord != null) {
            mainView.onSuggestions(string2Array(hotWord));
        } else {
            RetrofitManager.api().hotWord().enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    if (response.isSuccessful()) {
                        mainView.onSuggestions(string2Array(response.body().toString()));
                        PtrUtil.getInstance().start().put("hotWord", response.body().toString()).commit();
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("main", t.getMessage());
                }
            });
        }

    }

    public int getMenuId() {
        switch (get()) {
            case FRAGMENT_SUBSCRIBE:
                return R.id.action_subscribe;
            case FRAGMENT_HOT:
                return R.id.action_hot;
            case FRAGMENT_SHUHUI:
                return R.id.action_shuhui;
            default:
                return R.id.action_same;
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
        int defaultFragment = MainView.FRAGMENT_HOT;
        return PtrUtil.getInstance().getInt("default_fragment", defaultFragment);
    }

    public void switchFragment(int fragmentId) {
        save(fragmentId);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentMap.get(fragmentId);
        if (fragment != null && fragment == currentFragment)
            return;
        if (fragment == null) {
            switch (fragmentId) {
                case FRAGMENT_SUBSCRIBE:
                    fragment = SubscribeFragment.newInstance();
                    break;
                case FRAGMENT_HOT:
                    fragment = CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_HOT);
                    break;
                case FRAGMENT_SHUHUI:
                    fragment = CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_MOUSE);
                    break;
                case FRAGMENT_SAME:
                    fragment = CategoryFragment.newInstance(CategoryFragment.CLASSIFY_ID_SAME);
                    break;
            }
            fragmentMap.put(fragmentId, fragment);
        }
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (currentFragment == null) {
            transaction.add(R.id.container, fragment, fragment.toString());
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
