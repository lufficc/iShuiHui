package com.lufficc.ishuhui.data.source.comic;

import android.support.annotation.NonNull;

import com.lufficc.ishuhui.model.Comic;

import java.util.List;

/**
 * Created by lufficc on 2016/11/6.
 */

public interface ComicsDataSource {
    interface LoadComicsCallback {
        void onComicLoaded(List<Comic> comics);

        void onComicsEmpty();

        void onLoadedFailed(Throwable throwable);
    }

    interface GetComicCallback {

        void onComicLoaded(Comic comic);

        void onLoadedFailed();
    }

    interface SaveComicCallback {
        void onSuccess();

        void onFail();
    }


    void refresh(String classifyId);

    int deleteAll();

    int delete(String comicId);

    void getComics(String classifyId, int page, @NonNull LoadComicsCallback callback);

    void getComic(@NonNull String id, @NonNull GetComicCallback callback);

    void saveComic(Comic comic, int page, SaveComicCallback callback);

    void saveComics(List<Comic> comics, int page, SaveComicCallback callback);
}
