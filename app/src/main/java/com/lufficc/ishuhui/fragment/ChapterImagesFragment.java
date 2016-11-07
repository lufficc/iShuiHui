package com.lufficc.ishuhui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.preview.ImagesActivity;
import com.lufficc.ishuhui.adapter.ChapterImagesAdapter;
import com.lufficc.ishuhui.data.source.chapter.images.ChapterImagesDataSource;
import com.lufficc.ishuhui.data.source.chapter.images.ChapterImagesRepository;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.lightadapter.OnDataClickListener;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;

public class ChapterImagesFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnDataClickListener, ChapterImagesDataSource.LoadChapterImagesListCallback {

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ChapterImagesAdapter adapter;

    Comic comic;

    public static ChapterImagesFragment newInstance(@Nullable Comic comic) {
        ChapterImagesFragment fragment = new ChapterImagesFragment();
        Bundle args = new Bundle();
        args.putSerializable("comic", comic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
        if (getArguments() != null) {
            comic = (Comic) getArguments().getSerializable("comic");
        }
        init();
    }

    private void init() {
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        stateLayout.setInfoContentViewMargin(0, -256, 0, 0);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = new ChapterImagesAdapter(getActivity()));
        adapter.setOnDataClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        if (adapter.isDataEmpty()) {
            stateLayout.showProgressView();
        }
        if (comic == null) {
            ChapterImagesRepository.getInstance().getChapterImagesList(this);
        } else {
            ChapterImagesRepository.getInstance().getChapterImagesList(String.valueOf(comic.Id), this);
        }
    }

    @Override
    public CharSequence getTitle() {
        if(comic == null){
            return super.getTitle();
        }
        return comic.Title+"的下载";
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_categoty;
    }


    @Override
    public void onRefresh() {
        getData();
    }


    @Override
    public void onDataClick(int position, Object data) {
        ChapterImages chapterImages = (ChapterImages) data;
        ImagesActivity.showImages(getActivity(), chapterImages.getImages());
    }

    @Override
    public void onLoaded(List<ChapterImages> chapterImagesList) {
        if (chapterImagesList.isEmpty()) {
            stateLayout.showEmptyView();
        } else {
            stateLayout.showContentView();
            adapter.setData(chapterImagesList);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFailed() {
        swipeRefreshLayout.setRefreshing(false);
        stateLayout.showEmptyView();
    }
}