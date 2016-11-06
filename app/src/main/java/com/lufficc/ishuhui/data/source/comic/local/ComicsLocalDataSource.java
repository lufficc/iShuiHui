package com.lufficc.ishuhui.data.source.comic.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.data.source.comic.ComicsDataSource;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.Comic;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ComicsLocalDataSource implements ComicsDataSource {
    private static ComicsLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private ComicsLocalDataSource() {

    }

    public static ComicsLocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (ComicsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ComicsLocalDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void refresh(String classifyId) {
        delete(classifyId);
    }

    @Override
    public int deleteAll() {
        return Orm.getLiteOrm().deleteAll(Comic.class);
    }

    @Override
    public int delete(String classifyId) {
        return Orm.getLiteOrm().delete(new WhereBuilder(Comic.class).where("ClassifyId = ? ", classifyId));
    }

    @Override
    public void getSubscribedComics(@NonNull final LoadComicsCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<Comic> queryBuilder = new QueryBuilder<>(Comic.class)
                        .where("isSubscribe = ?", true)
                        .appendOrderDescBy("Title");
                final List<Comic> comics = Orm.getLiteOrm().query(queryBuilder);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (comics.isEmpty()) {
                            callback.onComicsEmpty();
                        } else {
                            callback.onComicLoaded(comics);
                        }
                    }
                });

            }
        });
    }

    @Override
    public void subscribe(final Comic comic, final boolean subscribe, final SubscribeComicCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    comic.isSubscribe = subscribe;
                    final long id = Orm.getLiteOrm().insert(comic, ConflictAlgorithm.Replace);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (id > 0) {
                                callback.onComicSubscribe(subscribe);
                            } else {
                                callback.onSubscribeFailed(new Exception("falied"));
                            }
                        }
                    });
                } catch (final Exception e) {
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
                    QueryBuilder<Comic> queryBuilder = null;
                    if ("0".equals(classifyId)) {
                        queryBuilder = new QueryBuilder<>(Comic.class)
                                .where("page = ?", page)
                                .appendOrderDescBy("Title");
                    } else {
                        queryBuilder = new QueryBuilder<>(Comic.class)
                                .where("ClassifyId = ? and page = ?", classifyId, page)
                                .appendOrderDescBy("Title");
                    }

                    final List<Comic> comics = Orm.getLiteOrm().query(queryBuilder);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (comics.isEmpty()) {
                                callback.onComicsEmpty();
                            } else {
                                callback.onComicLoaded(comics);
                            }
                        }
                    });
                } catch (final Exception e) {
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
    public void saveComic(final Comic comic, final int page, final SaveComicCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                comic.page = page;
                final long id = Orm.getLiteOrm().insert(comic, ConflictAlgorithm.Replace);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (id > 0) {
                            callback.onSuccess();
                        } else {
                            callback.onFail();
                        }
                    }
                });
            }
        });
    }




    @Override
    public void saveComics(final List<Comic> comics, final int page, final SaveComicCallback callback) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                boolean fail = false;
                try {
                    for (Comic comic : comics) {
                        comic.page = page;
                        Orm.getLiteOrm().insert(comic, ConflictAlgorithm.Replace);
                    }
                } catch (Exception e) {
                    fail = true;
                }
                final boolean finalFail = fail;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalFail) {
                            callback.onFail();
                        } else {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }
}
