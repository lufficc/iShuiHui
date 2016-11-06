package com.lufficc.ishuhui.data.source.comic.remote;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.lufficc.ishuhui.data.source.comic.ComicsDataSource;
import com.lufficc.ishuhui.data.source.file.local.FilesLocalDataSource;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.ComicsModel;
import com.lufficc.ishuhui.utils.AppUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Response;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ComicsRemoteDataSource implements ComicsDataSource {
    private static ComicsRemoteDataSource INSTANCE;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = AppUtils.getExecutorService();

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
    public void getSubscribedComics(@NonNull final LoadComicsCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Comic> comics;
                try {
                    comics = RetrofitManager.api().getSubscribedComics().execute().body().Return.List;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (comics.isEmpty()) {
                                callback.onComicsEmpty();
                            } else {
                                for (Comic c : comics) {
                                    c.isSubscribe = true;
                                }
                                Collections.sort(comics, new Comparator<Comic>() {
                                    @Override
                                    public int compare(Comic o1, Comic o2) {
                                        return o2.Title.compareTo(o1.Title);
                                    }
                                });
                                callback.onComicLoaded(comics);
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
    public void subscribe(final Comic comic, final boolean subscribe, final SubscribeComicCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<JsonObject> response = RetrofitManager.api().subscribe(String.valueOf(comic.Id), String.valueOf(subscribe), 2).execute();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                callback.onComicSubscribe(subscribe);
                            } else {
                                callback.onSubscribeFailed(new Exception(response.message()));
                            }
                        }
                    });
                } catch (final IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSubscribeFailed(e);
                        }
                    });
                }
            }
        });
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
                                List<Comic> comics = comicsModel.Return.List;
                                if (comics.isEmpty()) {
                                    callback.onComicsEmpty();
                                } else {
                                    Collections.sort(comics, new Comparator<Comic>() {
                                        @Override
                                        public int compare(Comic o1, Comic o2) {
                                            return o2.Title.compareTo(o1.Title);
                                        }
                                    });
                                    callback.onComicLoaded(comics);
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
