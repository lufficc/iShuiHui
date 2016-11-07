package com.lufficc.ishuhui.data.source.chapter.images;

import android.os.Handler;
import android.os.Looper;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.ChapterImages;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/6.
 */

public class ChapterImagesRepository implements ChapterImagesDataSource {
    private static ChapterImagesRepository INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private ChapterImagesRepository() {
    }

    public static ChapterImagesRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (ChapterImagesRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChapterImagesRepository();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public List<ChapterImages> getChapterImagesList(String comicId) {
        return Orm.getLiteOrm().cascade().query(new QueryBuilder<>(ChapterImages.class).where("comicId = ?", comicId).appendOrderDescBy("chapterNo"));
    }

    @Override
    public void getChapterImagesList(final String comicId, final LoadChapterImagesListCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<ChapterImages> chapterImagesList = getChapterImagesList(comicId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chapterImagesList == null) {
                            callback.onFailed();
                        } else {
                            callback.onLoaded(chapterImagesList);
                        }
                    }
                });
            }
        });
    }

    @Override
    public List<ChapterImages> getChapterImagesList() {
        return Orm.getLiteOrm().cascade().query(new QueryBuilder<>(ChapterImages.class).appendOrderDescBy("comicName").appendOrderDescBy("chapterNo"));
    }

    @Override
    public void getChapterImagesList(final LoadChapterImagesListCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<ChapterImages> chapterImagesList = getChapterImagesList();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chapterImagesList == null) {
                            callback.onFailed();
                        } else {
                            callback.onLoaded(chapterImagesList);
                        }
                    }
                });
            }
        });
    }
}
