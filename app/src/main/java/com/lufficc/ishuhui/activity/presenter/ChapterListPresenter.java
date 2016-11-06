package com.lufficc.ishuhui.activity.presenter;

import com.lufficc.ishuhui.activity.iview.ChapterListView;
import com.lufficc.ishuhui.data.source.chapter.ChaptersDataSource;
import com.lufficc.ishuhui.data.source.chapter.ChaptersRepository;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterListModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by lufficc on 2016/8/28.
 */

public class ChapterListPresenter {
    private final ChapterListView chapterListView;
    private Call<ChapterListModel> chapterListCall;

    public ChapterListPresenter(ChapterListView chapterListView) {
        this.chapterListView = chapterListView;
    }

    public void getData(int bookId, int PageIndex) {
        ChaptersRepository.getInstance().getChapters(String.valueOf(bookId), PageIndex, new ChaptersDataSource.LoadChaptersCallback() {
            @Override
            public void onChapterLoaded(List<Chapter> chapters) {
                chapterListView.onSuccess(chapters);
            }

            @Override
            public void onChaptersEmpty() {
                chapterListView.onSuccess(new ArrayList<Chapter>());
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                chapterListView.onFail(throwable);
            }
        });
    }

    public void refresh(String comicId) {
        ChaptersRepository.getInstance().refresh(comicId);
    }

    public void onDestroy() {
        if (chapterListCall != null && !chapterListCall.isCanceled())
            chapterListCall.cancel();
    }
}
