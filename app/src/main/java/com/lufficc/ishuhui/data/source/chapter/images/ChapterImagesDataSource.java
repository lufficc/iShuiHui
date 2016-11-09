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

    boolean has(String chapterId);

    int delete(ChapterImages chapterImages);

    long save(ChapterImages chapterImages);

    List<ChapterImages> getChapterImagesList(String comicId);

    void getChapterImagesList(String comicId, LoadChapterImagesListCallback callback);

    List<ChapterImages> getChapterImagesList();

    void getChapterImagesList(LoadChapterImagesListCallback callback);
}
