package com.lufficc.ishuhui.data.source.comic;

import android.support.annotation.NonNull;
import android.util.Log;

import com.litesuits.orm.db.assit.WhereBuilder;
import com.lufficc.ishuhui.data.source.comic.local.ComicsLocalDataSource;
import com.lufficc.ishuhui.data.source.comic.remote.ComicsRemoteDataSource;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.Comic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ComicsRepository implements ComicsDataSource {
    private static ComicsRepository INSTANCE;
    private Map<String, Boolean> dirties = new HashMap<>();
    private ComicsLocalDataSource localDataSource;
    private ComicsRemoteDataSource remoteDataSource;


    public static ComicsRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (ComicsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ComicsRepository();
                }
            }
        }
        return INSTANCE;
    }

    private ComicsRepository() {
        localDataSource = ComicsLocalDataSource.getInstance();
        remoteDataSource = ComicsRemoteDataSource.getInstance();
    }


    @Override
    public void refresh(String classifyId) {
        dirties.put(classifyId, true);
    }

    @Override
    public int deleteAll() {
        return localDataSource.deleteAll();
    }

    @Override
    public int delete(String comicId) {
        return localDataSource.delete(comicId);
    }

    private void getRemoteSubscribeComics(@NonNull final LoadComicsCallback callback) {
        remoteDataSource.getSubscribedComics(new LoadComicsCallback() {
            @Override
            public void onComicLoaded(List<Comic> comics) {
                if (subscribeComicsDirty) {
                    subscribeComicsDirty = false;
                }

                localDataSource.saveComics(comics, -1, new SaveComicCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                });
                callback.onComicLoaded(comics);
            }

            @Override
            public void onComicsEmpty() {
                callback.onComicsEmpty();
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });
    }

    @Override
    public void getSubscribedComics(@NonNull final LoadComicsCallback callback) {
        if (subscribeComicsDirty) {
            getRemoteSubscribeComics(callback);
            return;
        }
        localDataSource.getSubscribedComics(new LoadComicsCallback() {
            @Override
            public void onComicLoaded(List<Comic> comics) {
                callback.onComicLoaded(comics);
            }

            @Override
            public void onComicsEmpty() {
                getRemoteSubscribeComics(callback);
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });
    }

    @Override
    public void subscribe(final Comic comic, boolean subscribe, final SubscribeComicCallback callback) {

        remoteDataSource.subscribe(comic, subscribe, new SubscribeComicCallback() {
            @Override
            public void onComicSubscribe(boolean subscribe) {
                localDataSource.subscribe(comic, subscribe, callback);
            }

            @Override
            public void onSubscribeFailed(Throwable throwable) {
                callback.onSubscribeFailed(throwable);
            }
        });
    }

    private void getRemoteData(final String classifyId, final int page, @NonNull final LoadComicsCallback callback) {
        remoteDataSource.getComics(classifyId, page, new LoadComicsCallback() {
            @Override
            public void onComicLoaded(List<Comic> comics) {
                callback.onComicLoaded(comics);
                if (dirties.containsKey(classifyId) && dirties.get(classifyId)) {
                    dirties.put(classifyId, false);
                }
                Log.i("getComics", "remoteDataSource");
                saveComics(comics, page, new SaveComicCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onComicsEmpty() {
                callback.onComicsEmpty();
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });
    }

    @Override
    public void getComics(final String classifyId, final int page, @NonNull final LoadComicsCallback callback) {

        if (dirties.containsKey(classifyId) && dirties.get(classifyId)) {
            getRemoteData(classifyId, page, callback);
        }
        localDataSource.getComics(classifyId, page, new LoadComicsCallback() {
            @Override
            public void onComicLoaded(List<Comic> comics) {
                Log.i("getComics", "localDataSource");
                callback.onComicLoaded(comics);
            }

            @Override
            public void onComicsEmpty() {
                getRemoteData(classifyId, page, callback);
            }

            @Override
            public void onLoadedFailed(Throwable throwable) {
                callback.onLoadedFailed(throwable);
            }
        });
    }

    @Override
    public void getComic(@NonNull String id, @NonNull GetComicCallback callback) {

    }


    @Override
    public void saveComic(Comic comic, int page, SaveComicCallback callback) {
        localDataSource.saveComic(comic, page, callback);
    }

    @Override
    public void saveComics(List<Comic> Comics, int page, SaveComicCallback callback) {
        localDataSource.saveComics(Comics, page, callback);
    }

    private boolean subscribeComicsDirty = false;

    public void refreshSubscribedComics() {
        subscribeComicsDirty = true;
    }

    private void deleteSubscribed() {
        Orm.getLiteOrm().delete(new WhereBuilder(Comic.class).where("isSubscribe = ? ", true));
    }
}
