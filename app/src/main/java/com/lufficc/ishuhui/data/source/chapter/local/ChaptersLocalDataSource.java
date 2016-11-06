package com.lufficc.ishuhui.data.source.chapter.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.data.source.chapter.ChaptersDataSource;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.utils.AppUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ChaptersLocalDataSource implements ChaptersDataSource {
    private static ChaptersLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = AppUtils.getExecutorService();

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
        return Orm.getLiteOrm().deleteAll(Chapter.class);
    }

    @Override
    public int delete(String comicId) {
        return Orm.getLiteOrm().delete(new WhereBuilder(Chapter.class).where("BookId = ? ", comicId));
    }

    @Override
    public void getChapters(final String comicId, final int page, @NonNull final LoadChaptersCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                QueryBuilder<Chapter> queryBuilder = new QueryBuilder<>(Chapter.class)
                        .where(" BookId = ? and page = ? ", comicId, page)
                        .appendOrderDescBy("ChapterNo");
                final List<Chapter> chapters = Orm.getLiteOrm().query(queryBuilder);

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
                LiteOrm liteOrm = Orm.getLiteOrm();
                final long id = liteOrm.insert(chapter, ConflictAlgorithm.Replace);
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
                        Orm.getLiteOrm().insert(chapter, ConflictAlgorithm.Ignore);
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
