package com.lufficc.ishuhui.data.source.chapter.images;

import android.os.Handler;
import android.os.Looper;

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
    public ChapterImages getChapterImages(String chapterId) {
        return Orm.getLiteOrm().cascade().queryById(chapterId, ChapterImages.class);
    }

    @Override
    public void getChapterImages(final String chapterId, final LoadChapterImagesCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final ChapterImages chapterImages = getChapterImages(chapterId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chapterImages == null) {
                            callback.onFailed();
                        } else {
                            callback.onLoaded(chapterImages);
                        }
                    }
                });

            }
        });
    }

    @Override
    public List<ChapterImages> getChapterImagesList() {
        return Orm.getLiteOrm().cascade().query(ChapterImages.class);
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
