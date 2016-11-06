package com.lufficc.ishuhui.data.source.chapter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lufficc.ishuhui.data.source.chapter.local.ChaptersLocalDataSource;
import com.lufficc.ishuhui.data.source.chapter.remote.ChaptersRemoteDataSource;
import com.lufficc.ishuhui.model.Chapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ChaptersRepository implements ChaptersDataSource {
    private static ChaptersRepository INSTANCE;
    private Map<String, Boolean> dirties = new HashMap<>();

    private ChaptersLocalDataSource localDataSource;
    private ChaptersRemoteDataSource remoteDataSource;


    public static ChaptersRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (ChaptersRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChaptersRepository();
                }
            }
        }
        return INSTANCE;
    }

    private ChaptersRepository() {
        localDataSource = ChaptersLocalDataSource.getInstance();
        remoteDataSource = ChaptersRemoteDataSource.getInstance();
    }


    @Override
    public void refresh(String comicId) {
        dirties.put(comicId, true);
    }

    @Override
    public int deleteAll() {
        return localDataSource.deleteAll();
    }

    @Override
    public int delete(String comicId) {
        return localDataSource.delete(comicId);
    }

    private void getRemoteData(final String comicId, final int page, @NonNull final LoadChaptersCallback callback) {

        remoteDataSource.getChapters(comicId, page, new LoadChaptersCallback() {
            @Override
            public void onChapterLoaded(List<Chapter> chapters) {
                callback.onChapterLoaded(chapters);
                if (dirties.containsKey(comicId) && dirties.get(comicId)) {
                    dirties.put(comicId, false);
                }
                Log.i("getChapters","remoteDataSource");
                saveChapters(chapters, page, new SaveChapterCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFail() {
                    }
                });
            }

            @Override
            public void onChaptersEmpty() {
                callback.onChaptersEmpty();
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });
    }

    @Override
    public void getChapters(final String comicId, final int page, @NonNull final LoadChaptersCallback callback) {

        if (dirties.containsKey(comicId) && dirties.get(comicId)) {
            getRemoteData(comicId, page, callback);
            return;
        }

        localDataSource.getChapters(comicId, page, new LoadChaptersCallback() {
            @Override
            public void onChapterLoaded(List<Chapter> chapters) {
                Log.i("getChapters","localDataSource");
                callback.onChapterLoaded(chapters);
            }

            @Override
            public void onChaptersEmpty() {
                getRemoteData(comicId, page, callback);
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });

    }

    @Override
    public void getChapter(@NonNull String id, @NonNull GetChapterCallback callback) {

    }

    @Override
    public void saveChapter(Chapter chapter, int page, SaveChapterCallback callback) {
        localDataSource.saveChapter(chapter, page, callback);
    }

    @Override
    public void saveChapters(List<Chapter> chapters, int page, SaveChapterCallback callback) {
        localDataSource.saveChapters(chapters, page, callback);
    }
}
