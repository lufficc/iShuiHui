package com.lufficc.ishuhui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.MainView;
import com.lufficc.ishuhui.activity.presenter.MainPresenter;
import com.lufficc.ishuhui.fragment.SearchFragment;
import com.lufficc.ishuhui.manager.FirManager;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FirLatestModel;
import com.lufficc.ishuhui.model.User;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.KeyboardUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements
        MainView,
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        MaterialSearchView.OnQueryTextListener {
    @BindView(R.id.fab)
    FloatingActionButton actionButton;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;

    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    @BindView(R.id.logoutBtn)
    Button logoutBtn;

    TextView email;

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        MobclickAgent.enableEncrypt(true);
        init();
        onLogin(User.getInstance());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    ImageView profile_image;

    private void init() {
        mainPresenter = new MainPresenter(this, getSupportFragmentManager());
        searchView.setOnQueryTextListener(this);
        profile_image = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(mainPresenter.getMenuId()).setChecked(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        profile_image.setOnClickListener(this);
        email.setOnClickListener(this);

        if (User.getInstance().isLogin()) {
            logoutBtn.setVisibility(View.GONE);
            onLogin(User.getInstance());
        } else {
            email.setText("点击登陆");
        }

        actionButton.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        actionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean see = PtrUtil.getInstance().getInt("latest_see_id", -1) > 0;
                if (see) {
                    toast(PtrUtil.getInstance().getString("latest_see_title", "未知漫画"));
                } else {
                    toast("最近没看漫画");
                }
                return true;
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_about:
                AboutActivity.about(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (hideSearchFragment())
            return;
        moveTaskToBack(true);
    }

    private boolean hideSearchFragment() {
        if (searchFragment != null && searchFragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.hide(searchFragment).show(mainPresenter.getCurrentFragment());
            setTitle(mainPresenter.getCurrentFragment().toString());
            transaction.commit();
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            return true;
        }
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                String comic = PtrUtil.getInstance().getString(ChapterListActivity.KEY_LAST_SEE, null);
                if (comic != null) {
                    ChapterListActivity.showChapterList(this, JsonUtil.fromJson(comic, Comic.class));
                } else {
                    toast("最近没看漫画");
                }
                break;
            case R.id.profile_image:
            case R.id.email:
                if (!User.getInstance().isLogin()) {
                    LoginActivity.login(this);
                    drawerLayout.closeDrawers();
                } else {
                    toast("谢谢使用鼠绘漫画");
                }
                break;
            case R.id.logoutBtn:
                drawerLayout.closeDrawers();
                Snackbar.make(toolbar, "确定退出登录吗?", Snackbar.LENGTH_LONG)
                        .setAction("登出", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                User.getInstance().logout();
                                toast("已退出登录");
                            }
                        }).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(@Nullable User user) {
        if (user != null) {
            logoutBtn.setVisibility(View.VISIBLE);
            email.setText(user.getEmail());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(User.EventLogout eventLogout) {
        email.setText("点击登录");
        logoutBtn.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_subscribe:
                mainPresenter.switchFragment(FRAGMENT_SUBSCRIBE);
                break;
            case R.id.action_hot:
                mainPresenter.switchFragment(FRAGMENT_HOT);
                break;
            case R.id.action_shuhui:
                mainPresenter.switchFragment(FRAGMENT_SHUHUI);
                break;
            case R.id.action_same:
                mainPresenter.switchFragment(FRAGMENT_SAME);
                break;
        }
        hideSearchFragment();
        drawerLayout.closeDrawers();
        return true;
    }

    private SearchFragment searchFragment;

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (searchFragment == null) {
            searchFragment = SearchFragment.search(query);
        } else {
            searchFragment.refresh(query);
        }
        if (!searchFragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.hide(mainPresenter.getCurrentFragment());
            if (!searchFragment.isAdded()) {
                transaction.add(R.id.container, searchFragment);
            } else {
                transaction.show(searchFragment);
            }
            transaction.commit();
            setTitle(getString(R.string.search));
        }
        KeyboardUtil.hideSoftInput(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onSuggestions(String[] strings) {
        searchView.setSuggestions(strings);
    }

    @Override
    public void onShowFragment(Fragment fragment) {
        setTitle(fragment.toString());
    }

    @Override
    public void onUpdate(final FirLatestModel firLatestModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(firLatestModel.changelog+String.format(Locale.CHINA,"[%d K]",firLatestModel.binary.fsize / 1000))
                .setTitle("新版本")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(firLatestModel);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void download(FirLatestModel firLatestModel) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("下载中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        FirManager.instance().downloadApk(firLatestModel.install_url, new FirManager.DownloadListener() {
            @Override
            public void onProgress(int percent) {
                progressDialog.setProgress(percent);
            }

            @Override
            public void onError(Throwable throwable) {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(Uri apk) {
                FirManager.install(MainActivity.this,apk);
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

}
