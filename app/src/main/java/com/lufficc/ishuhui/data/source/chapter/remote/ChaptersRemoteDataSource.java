package com.lufficc.ishuhui.data.source.chapter.remote;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.chapter.ChaptersDataSource;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterListModel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ChaptersRemoteDataSource implements ChaptersDataSource {
    private static ChaptersRemoteDataSource INSTANCE;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private ChaptersRemoteDataSource() {
    }

    public static ChaptersRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (ChaptersRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChaptersRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void refresh(String comicId) {

    }

    @Override
    public int deleteAll() {
        return 0;
    }

    @Override
    public int delete(String comicId) {
        return 0;
    }

    @Override
    public void getChapters(final String comicId, final int page, @NonNull final LoadChaptersCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChapterListModel finalModel = RetrofitManager.api().getComicChapters(comicId, page).execute().body();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalModel != null) {
                                if (finalModel.Return.List.isEmpty()) {
                                    callback.onChaptersEmpty();
                                } else {
                                    callback.onChapterLoaded(finalModel.Return.List);
                                }
                            }
                        }
                    });
                } catch (final IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoadedFailed(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getChapter(@NonNull String id, @NonNull GetChapterCallback callback) {

    }

    @Override
    public void saveChapter(Chapter chapter, int page, SaveChapterCallback callback) {

    }

    @Override
    public void saveChapters(List<Chapter> chapters, int page, SaveChapterCallback callback) {

    }
}
