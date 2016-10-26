package com.lufficc.ishuhui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.LoginActivity;
import com.lufficc.ishuhui.adapter.ComicAdapter;
import com.lufficc.ishuhui.fragment.IView.IView;
import com.lufficc.ishuhui.fragment.presenter.SubscribeFragmentPresenter;
import com.lufficc.ishuhui.model.ComicModel;
import com.lufficc.ishuhui.model.User;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import retrofit2.Call;

public class SubscribeFragment extends BaseFragment implements IView<ComicModel>, SwipeRefreshLayout.OnRefreshListener {

    ComicAdapter adapter;
    LoadMoreFooterModel footerModel;

    SubscribeFragmentPresenter subscribeFragmentPresenter;
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static SubscribeFragment newInstance() {
        return new SubscribeFragment();
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
        init();
    }

    @Override
    public String toString() {
        return "订阅";
    }

    private void init() {
        EventBus.getDefault().register(this);
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        stateLayout.setInfoContentViewMargin(0, -256, 0, 0);
        subscribeFragmentPresenter = new SubscribeFragmentPresenter(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new ComicAdapter(getContext(), true);
        footerModel = adapter.getLoadMoreFooterModel();
        footerModel.noMoreData();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private boolean checkLogin() {
        if (!User.getInstance().isLogin()) {
            stateLayout.showEmptyView("您还没有登录");
            stateLayout.setEmptyAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast("您还没有登录");
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            return false;
        }
        return true;
    }

    private void getData() {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();

        if (checkLogin()) {
            subscribeFragmentPresenter.getSubscribedComics();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_categoty;
    }

    @Override
    public void onSuccess(ComicModel comicModel) {
        stateLayout.showContentView();
        if (comicModel.Return.List.isEmpty()) {
            stateLayout.showEmptyView("您啥也没订阅");
        } else {
            adapter.setData(comicModel.Return.List);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(User user) {
        getData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(User.EventLogout eventLogout) {
        adapter.clearData();
        getData();
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        subscribeFragmentPresenter.onDestroy();
    }

    @Override
    public void onFailure(Call call, Throwable e) {
        if (adapter.isDataEmpty()) {
            stateLayout.showErrorView(e.getMessage());
        } else {
            footerModel.errorOccur("加载出错," + e.getMessage());
        }
    }

    @Override
    public void onRefresh() {
        if (adapter.isDataEmpty()) {
            stateLayout.showProgressView();
        }
        getData();
    }
}
