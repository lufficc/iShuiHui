package com.lufficc.ishuhui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.ComicAdapter;
import com.lufficc.ishuhui.fragment.IView.IView;
import com.lufficc.ishuhui.fragment.presenter.CategoryFragmentPresenter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;

public class CategoryFragment extends BaseFragment implements IView<List<Comic>>, SwipeRefreshLayout.OnRefreshListener, LoadMoreFooterModel.LoadMoreListener {
    private static final String CLASSIFY_ID = "CLASSIFY_ID";

    //ClassifyId   分类标识，0热血，1国产，2同人，3鼠绘
    public static final String CLASSIFY_ID_HOT = "0";
    public static final String CLASSIFY_ID_CHINA = "1";
    public static final String CLASSIFY_ID_SAME = "2";
    public static final String CLASSIFY_ID_MOUSE = "3";
    public static final String CLASSIFY_ID_ALL = "[0,1,2,3]";
    private String classifyId;
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    CategoryFragmentPresenter presenter;
    ComicAdapter adapter;
    LoadMoreFooterModel loadMoreFooterModel;

    private int PageIndex = 0, prevPage;

    public static CategoryFragment newInstance(String classifyId) {
        CategoryFragment fragment = new CategoryFragment();
        String title = "热血";
        Bundle args = new Bundle();
        switch (classifyId) {
            case CLASSIFY_ID_HOT:
                title = "热血";
                break;
            case CLASSIFY_ID_CHINA:
                title = "国产";
                break;
            case CLASSIFY_ID_SAME:
                title = "同人";
                break;
            case CLASSIFY_ID_MOUSE:
                title = "鼠绘";
                break;
        }
        args.putString(CLASSIFY_ID, classifyId);
        fragment.setArguments(args);
        fragment.title = title;
        return fragment;
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
        if (getArguments() != null) {
            classifyId = getArguments().getString(CLASSIFY_ID);
        }
        init();
    }

    private String title;

    @Override
    public CharSequence getTitle() {
        return title;
    }

    private void init() {
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        stateLayout.setInfoContentViewMargin(0, -256, 0, 0);
        presenter = new CategoryFragmentPresenter(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ComicAdapter(getActivity()));
        loadMoreFooterModel = adapter.getLoadMoreFooterModel();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        loadMoreFooterModel.setLoadMoreListener(this);
        getData();
    }

    private void getData() {
        if (adapter.getData().isEmpty())
            stateLayout.showProgressView();
        presenter.getData(classifyId, PageIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_categoty;
    }

    @Override
    public void onSuccess(List<Comic> comics) {
        stateLayout.showContentView();
        if (comics.isEmpty()) {
            loadMoreFooterModel.noMoreData();
        } else {
            if (PageIndex == 0) {
                adapter.setData(comics);
            } else {
                adapter.addData(comics);
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(Throwable e) {
        if (adapter.getData().isEmpty()) {
            stateLayout.showErrorView(e.getMessage());
        } else {
            loadMoreFooterModel.errorOccur("加载出错");
        }
    }

    @Override
    public void onRefresh() {
        presenter.refresh(classifyId);
        loadMoreFooterModel.canLoadMore();
        prevPage = PageIndex;
        PageIndex = 0;
        if (adapter.getData().isEmpty()) {
            stateLayout.showProgressView();
        }
        getData();
    }

    @Override
    public void onLoadMore() {
        prevPage = PageIndex;
        ++PageIndex;
        getData();
    }
}