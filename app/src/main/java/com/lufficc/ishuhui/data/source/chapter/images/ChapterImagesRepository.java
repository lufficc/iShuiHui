package com.lufficc.ishuhui.data.source.chapter.images;

import android.os.Handler;
import android.os.Looper;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/6.
 */

public class ChapterImagesRepository implements ChapterImagesDataSource {
    private static ChapterImagesRepository INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<String, Boolean> downloadedMap = new HashMap<>();
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
    public boolean has(String chapterId) {
        boolean downloaded;
        synchronized (this) {
            if (downloadedMap.containsKey(chapterId)) {
                downloaded = downloadedMap.get(chapterId);
            } else {
                downloaded = Orm.getLiteOrm().queryCount(new QueryBuilder<>(ChapterImages.class).where("chapterId = ? ", chapterId)) > 0;
                downloadedMap.put(chapterId, downloaded);
            }
        }
        return downloaded;
    }

    @Override
    public int delete(ChapterImages chapterImages) {
        synchronized (this) {
            if (downloadedMap.containsKey(chapterImages.getChapterId())) {
                downloadedMap.put(chapterImages.getChapterId(), false);
            }
        }
        return Orm.getLiteOrm().delete(chapterImages);
    }

    @Override
    public long save(ChapterImages chapterImages) {
        if (chapterImages == null)
            return 0;
        long id = Orm.getLiteOrm().cascade().insert(chapterImages, ConflictAlgorithm.Replace);
        if (id > 0) {
            synchronized (this) {
                downloadedMap.put(chapterImages.getChapterId(), true);
            }
        }
        return id;
    }

    @Override
    public List<ChapterImages> getChapterImagesList(String comicId) {
        List<ChapterImages> chapterImagesList = Orm.getLiteOrm().cascade().query(new QueryBuilder<>(ChapterImages.class).where("comicId = ?", comicId).appendOrderDescBy("sort"));
        if (chapterImagesList != null) {
            for (ChapterImages chapterImages : chapterImagesList) {
                sort(chapterImages);
            }
        }
        return chapterImagesList;
    }

    private void sort(ChapterImages chapterImages) {
        if (chapterImages.getImages() == null) return;
        Collections.sort(chapterImages.getImages(), new Comparator<FileEntry>() {
            @Override
            public int compare(FileEntry o1, FileEntry o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
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
        List<ChapterImages> chapterImagesList = Orm.getLiteOrm().cascade().query(new QueryBuilder<>(ChapterImages.class).appendOrderDescBy("comicName").appendOrderDescBy("sort"));
        if (chapterImagesList != null) {
            for (ChapterImages chapterImages : chapterImagesList) {
                sort(chapterImages);
            }
        }
        return chapterImagesList;
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
