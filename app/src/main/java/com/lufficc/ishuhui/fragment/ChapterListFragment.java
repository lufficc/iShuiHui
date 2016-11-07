package com.lufficc.ishuhui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.ChapterListView;
import com.lufficc.ishuhui.activity.presenter.ChapterListPresenter;
import com.lufficc.ishuhui.adapter.ChapterListAdapter;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.service.DownloadManager;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.ishuhui.widget.DefaultItemDecoration;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;

public class ChapterListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        ChapterListView, DownloadManager.DownLoadListener {
    public static final String COMIC = "COMIC";

    public static final String KEY_LAST_SEE = "KEY_LAST_SEE";

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ChapterListAdapter adapter;

    LoadMoreFooterModel footerModel;

    ChapterListPresenter chapterListPresenter;

    private Comic comic;
    HeaderViewProvider.Header header;
    private int PageIndex = 0;
    private String title;
    private int bookId;

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
    public String getTitle() {
        return title;
    }

    private void init() {
        chapterListPresenter = new ChapterListPresenter(this);
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


        adapter = initAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        getData();
    }

    private ChapterListAdapter initAdapter() {
        adapter = new ChapterListAdapter(getContext(), comic);
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


    private void getData() {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        chapterListPresenter.getData(bookId, PageIndex);
    }

    @Override
    public void onPause() {
        DownloadManager.getInstance().removeDownLoadListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        DownloadManager.getInstance().addDownLoadListener(this);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        chapterListPresenter.onDestroy();
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_chapter_list;
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
    public void onDownloadStart(String comicId, String chapterId) {
        adapter.getChapterListViewHolderProvider().onDownloadStart(comicId, chapterId);
    }

    @Override
    public void onChapterDownloaded(String comicId, String chapterId) {
        adapter.getChapterListViewHolderProvider().onChapterDownloaded(comicId, chapterId);
    }

    @Override
    public void onFileDownloaded(FileEntry onException) {
        adapter.getChapterListViewHolderProvider().onFileDownloaded(onException);
    }

    @Override
    public void onException(FileEntry fileEntry, Exception e) {
        adapter.getChapterListViewHolderProvider().onException(fileEntry, e);
    }
}