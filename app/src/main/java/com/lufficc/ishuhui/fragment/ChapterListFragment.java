package com.lufficc.ishuhui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.LoginActivity;
import com.lufficc.ishuhui.activity.iview.ChapterListView;
import com.lufficc.ishuhui.activity.iview.SubscribeView;
import com.lufficc.ishuhui.activity.presenter.ChapterListPresenter;
import com.lufficc.ishuhui.activity.presenter.SubscribePresenter;
import com.lufficc.ishuhui.adapter.ChapterListAdapter;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.User;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.ishuhui.utils.SubscribeUtil;
import com.lufficc.ishuhui.widget.DefaultItemDecoration;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;

public class ChapterListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener,
        ChapterListView,
        SubscribeView {
    public static final String COMIC = "COMIC";

    public static final String KEY_LAST_SEE = "KEY_LAST_SEE";

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.fab_subscribe)
    FloatingActionButton fab_subscribe;

    ChapterListAdapter adapter;

    LoadMoreFooterModel footerModel;

    ChapterListPresenter chapterListPresenter;
    SubscribePresenter subscribePresenter;

    private Comic comic;
    HeaderViewProvider.Header header;
    private int PageIndex = 0;
    private String title;
    private int bookId;
    private boolean isSubscribed = false;

    public static ChapterListFragment newInstance(Comic comic) {
        ChapterListFragment fragment = new ChapterListFragment();
        Bundle args = new Bundle();
        args.putSerializable(COMIC, comic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
        if (getArguments() != null) {
            stateLayout.showProgressView();
            comic = (Comic) getArguments().getSerializable(COMIC);
            bookId = comic.Id;
            title = comic.Title;
        }
        init();
        PtrUtil.getInstance().start()
                .put(KEY_LAST_SEE, JsonUtil.toJson(comic))
                .commit();
    }


    @Override
    public String toString() {
        return title;
    }

    private void init() {
        chapterListPresenter = new ChapterListPresenter(this);
        subscribePresenter = new SubscribePresenter(this);
        recyclerView.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(getContext(), R.color.white),
                ContextCompat.getColor(getContext(), R.color.divider),
                getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)
        ));
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        isSubscribed = SubscribeUtil.isSubscribed(bookId);
        if (isSubscribed) {
            fab_subscribe.setImageResource(R.mipmap.ic_done);
        }
        adapter = initAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);


        fab_subscribe.setOnClickListener(this);
        getData();
    }

    private ChapterListAdapter initAdapter() {
        adapter = new ChapterListAdapter(getContext(),comic);
        adapter.addHeader(header = new HeaderViewProvider.Header());
        header.setTitle(comic.Title);
        header.setDes(comic.Explain);
        header.setUrl(comic.FrontCover);
        footerModel = adapter.getLoadMoreFooterModel();
        footerModel.setLoadMoreListener(new LoadMoreFooterModel.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                ++PageIndex;
                getData();
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        return adapter;
    }

    private void subscribe() {
        if (!User.getInstance().isLogin()) {
            LoginActivity.login(getActivity());
            toast("登陆后才能订阅吆");
            return;
        }
        subscribePresenter.subscribe(comic, isSubscribed);
    }

    private void getData() {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        chapterListPresenter.getData(bookId, PageIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chapterListPresenter.onDestroy();
        subscribePresenter.onDestroy();
    }




    @Override
    public int getLayoutId() {
        return R.layout.fragment_chapter_list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_subscribe:
                subscribe();
                break;
        }
    }

    @Override
    public void onRefresh() {
        chapterListPresenter.refresh(String.valueOf(bookId));
        footerModel.canLoadMore();
        PageIndex = 0;
        getData();
    }


    @Override
    public void onSuccess(List<Chapter> chapters) {
        if (chapters.isEmpty()) {
            footerModel.noMoreData();
        } else {
            if (PageIndex == 0) {
                adapter.setData(chapters);
            } else {
                adapter.addData(chapters);
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        if (adapter.isDataEmpty()) {
            stateLayout.showEmptyView();
        } else {
            stateLayout.showContentView();
        }
    }

    @Override
    public void onFail(Throwable t) {
        swipeRefreshLayout.setRefreshing(false);
        if (adapter.isDataEmpty()) {
            stateLayout.showErrorView(t.getMessage());
        }
    }

    @Override
    public void onSubscribe(boolean isSubscribed) {
        if (isSubscribed) {
            toast("订阅成功");
        } else {
            toast("已取消订阅");
        }
        ChapterListFragment.this.isSubscribed = isSubscribed;
        if (isSubscribed) {
            fab_subscribe.setImageResource(R.mipmap.ic_done);
        } else {
            fab_subscribe.setImageResource(R.mipmap.ic_add);
        }
    }

    @Override
    public void onFailSubscribe(String msg) {
        toast(msg);
    }

}