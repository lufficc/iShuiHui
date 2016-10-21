package com.lufficc.ishuhui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.ComicAdapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.ComicModel;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends BaseFragment {
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ComicAdapter adapter;

    LoadMoreFooterModel footerModel;

    private String keyword;

    public static SearchFragment search(String keyword) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(getBundle(keyword));
        return fragment;
    }

    public static Bundle getBundle(String keyword) {
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        return bundle;
    }


    public void refresh(String keyword) {
        this.keyword = keyword;
        getData();
    }

    @Override
    public void initialize(@Nullable Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString("keyword");
        }
        init();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getData();
        }
    };

    private void init() {
        stateLayout.setErrorAndEmptyAction(onClickListener);
        stateLayout.setInfoContentViewMargin(0, -256, 0, 0);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ComicAdapter(getContext()));
        footerModel = adapter.getLoadMoreFooterModel();
        footerModel.noMoreData();
        getData();
    }

    private void getData() {
        stateLayout.showProgressView();
        RetrofitManager.api().search(keyword)
                .enqueue(new Callback<ComicModel>() {
                    @Override
                    public void onResponse(Call<ComicModel> call, Response<ComicModel> response) {
                        footerModel.noMoreData();
                        if (response.isSuccessful()) {
                            List<Comic> comics = response.body().Return.List;
                            if (comics.isEmpty()) {
                                stateLayout.showEmptyView();
                                adapter.clearData();
                            } else {
                                stateLayout.showContentView();
                                adapter.setData(response.body().Return.List);
                            }

                        } else {
                            toast(response.message());
                            stateLayout.showErrorView(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ComicModel> call, Throwable t) {
                        footerModel.noMoreData();
                        toast(t.getMessage());
                        stateLayout.showErrorView(t.getMessage());
                    }
                });
    }

    @Override
    public String toString() {
        return "搜索";
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }
}
