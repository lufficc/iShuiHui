package com.lufficc.ishuhui.data.source.chapter;

import android.support.annotation.NonNull;

import com.lufficc.ishuhui.model.Chapter;

import java.util.List;

/**
 * Created by lufficc on 2016/11/6.
 */

public interface ChaptersDataSource {
    interface LoadChaptersCallback {
        void onChapterLoaded(List<Chapter> chapters);

        void onChaptersEmpty();

        void onLoadedFailed(Throwable throwable);
    }

    interface GetChapterCallback {

        void onChapterLoaded(Chapter Chapter);

        void onLoadedFailed();
    }

    interface SaveChapterCallback {
        void onSuccess();

        void onFail();
    }


    void refresh(String comicId);

    int deleteAll();

    int delete(String comicId);

    void getChapters(String comicId, int page, @NonNull LoadChaptersCallback callback);

    void getChapter(@NonNull String id, @NonNull GetChapterCallback callback);

    void saveChapter(Chapter chapter, int page, SaveChapterCallback callback);

    void saveChapters(List<Chapter> chapters, int page, SaveChapterCallback callback);
}
