package com.lufficc.ishuhui.data.source.comic.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.comic.ComicsDataSource;
import com.lufficc.ishuhui.model.Comic;
import com.orm.SugarRecord;
import com.orm.query.Select;

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
        return SugarRecord.deleteAll(Comic.class);
    }

    @Override
    public int delete(String classifyId) {
        return SugarRecord.deleteAll(Comic.class, "classify_id = ?", classifyId);
    }

    @Override
    public void getComics(final String classifyId, final int page, @NonNull final LoadComicsCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Comic> comics = Select.from(Comic.class)
                        .where("classify_id = ? and page = ?", new String[]{classifyId, String.valueOf(page)})
                        .list();
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
    public void getComic(@NonNull String id, @NonNull GetComicCallback callback) {

    }


    @Override
    public void saveComic(final Comic comic, final int page, final SaveComicCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                comic.page = page;
                SugarRecord.save(comic.LastChapter);
                final long id = SugarRecord.save(comic);
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

    public static Comic findOne(String comicId) {
        List<Comic> comics = SugarRecord.find(Comic.class, "id = ?", comicId);
        return comics.isEmpty() ? null : comics.get(0);
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
                        Comic old = findOne(String.valueOf(comic.Id));
                        if (old != null) {
                            SugarRecord.delete(old);
                        }
                        SugarRecord.save(comic.LastChapter);
                    }
                    SugarRecord.saveInTx(comics);
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
