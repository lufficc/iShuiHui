package com.lufficc.ishuhui.data.source.chapter.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.chapter.ChaptersDataSource;
import com.lufficc.ishuhui.model.Chapter;
import com.orm.SugarRecord;
import com.orm.query.Select;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ChaptersLocalDataSource implements ChaptersDataSource {
    private static ChaptersLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private ChaptersLocalDataSource() {

    }

    public static ChaptersLocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (ChaptersLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChaptersLocalDataSource();
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
        return SugarRecord.deleteAll(Chapter.class);
    }

    @Override
    public int delete(String comicId) {
        return SugarRecord.deleteAll(Chapter.class, "book_id = ?", comicId);
    }

    @Override
    public void getChapters(final String comicId, final int page, @NonNull final LoadChaptersCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Chapter> chapters = Select.from(Chapter.class)
                        .where("book_id = ? and page = ?",new String[]{comicId, String.valueOf(page)})
                        .orderBy("chapter_no desc")
                        .list();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (chapters.isEmpty()) {
                            callback.onChaptersEmpty();
                        } else {
                            callback.onChapterLoaded(chapters);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void getChapter(@NonNull String id, @NonNull GetChapterCallback callback) {

    }

    @Override
    public void saveChapter(final Chapter chapter, final int page, final SaveChapterCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                chapter.page = page;
                final long id = SugarRecord.save(chapter);
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
    public void saveChapters(final List<Chapter> chapters, final int page, final SaveChapterCallback callback) {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                boolean fail = false;
                try {
                    for (Chapter chapter : chapters) {
                        chapter.page = page;
                    }

                    SugarRecord.saveInTx(chapters);
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
