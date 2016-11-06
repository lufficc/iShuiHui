package com.lufficc.ishuhui.data.source.comic.remote;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.comic.ComicsDataSource;
import com.lufficc.ishuhui.data.source.file.local.FilesLocalDataSource;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.ComicsModel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ComicsRemoteDataSource implements ComicsDataSource {
    private static ComicsRemoteDataSource INSTANCE;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private ComicsRemoteDataSource() {
    }

    public static ComicsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (FilesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ComicsRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void refresh(String classifyId) {

    }

    @Override
    public int deleteAll() {
        return 0;
    }

    @Override
    public int delete(String classifyId) {
        return 0;
    }

    @Override
    public void getComics(final String classifyId, final int page, @NonNull final LoadComicsCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ComicsModel comicsModel = RetrofitManager.api().getComics(classifyId, page).execute().body();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (comicsModel != null) {
                                if (comicsModel.Return.List.isEmpty()) {
                                    callback.onComicsEmpty();
                                } else {
                                    callback.onComicLoaded(comicsModel.Return.List);
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
    public void getComic(@NonNull String id, @NonNull GetComicCallback callback) {

    }

    @Override
    public void saveComic(Comic comic, int page, SaveComicCallback callback) {

    }

    @Override
    public void saveComics(List<Comic> comics, int page, SaveComicCallback callback) {

    }
}
