package com.lufficc.ishuhui.data.source.chapter.images;

import com.lufficc.ishuhui.model.ChapterImages;

import java.util.List;

/**
 * Created by lufficc on 2016/11/6.
 */

public interface ChapterImagesDataSource {
    interface LoadChapterImagesListCallback {
        void onLoaded(List<ChapterImages> chapterImagesList);

        void onFailed();
    }

    interface LoadChapterImagesCallback {
        void onLoaded(ChapterImages chapterImages);

        void onFailed();
    }

    ChapterImages getChapterImages(String chapterId);

    void getChapterImages(String chapterId, LoadChapterImagesCallback callback);

    List<ChapterImages> getChapterImagesList();

    void getChapterImagesList(LoadChapterImagesListCallback callback);
}
