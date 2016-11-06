package com.lufficc.ishuhui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.ChapterListAdapter;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class ChapterListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String CHAPTER_LIST = "CHAPTER_LIST";

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ChapterListAdapter adapter;
    LoadMoreFooterModel loadMoreFooterModel;

    private List<Chapter> chapters;

    public static ChapterListFragment newInstance(List<Chapter> chapters) {
        ChapterListFragment fragment = new ChapterListFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHAPTER_LIST, (Serializable) chapters);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
        if (getArguments() != null) {
            chapters = (List<Chapter>) getArguments().getSerializable(CHAPTER_LIST);
        }
        init();
    }

    private String title = "Chapter";

    @Override
    public String toString() {
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
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ChapterListAdapter(getActivity(), null));
        loadMoreFooterModel = adapter.getLoadMoreFooterModel();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        loadMoreFooterModel.noMoreData();
        getData();
    }

    private void getData() {


    }

    public int getLayoutId() {
        return R.layout.fragment_categoty;
    }


    @Override
    public void onRefresh() {
        getData();
    }

}