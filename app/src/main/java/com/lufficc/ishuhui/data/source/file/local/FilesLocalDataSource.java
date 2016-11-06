package com.lufficc.ishuhui.data.source.file.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.data.source.file.FilesDataSource;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.utils.AppUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FilesLocalDataSource implements FilesDataSource {
    private static FilesLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = AppUtils.getExecutorService();

    private FilesLocalDataSource() {

    }

    public static FilesLocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (FilesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilesLocalDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void refresh(String chapterId) {

    }

    @Override
    public List<FileEntry> getFiles(String chapterId) {
        QueryBuilder<FileEntry> queryBuilder = new QueryBuilder<>(FileEntry.class)
                .where("chapterId = ? ", chapterId)
                .appendOrderAscBy("title");
        return Orm.getLiteOrm().query(queryBuilder);
    }

    @Override
    public void getFiles(final String chapterId, @NonNull final LoadFilesCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<FileEntry> fileEntries = getFiles(chapterId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (fileEntries.isEmpty()) {
                            callback.onLoadedFailed();
                        } else {
                            callback.onFileLoaded(fileEntries);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void getFile(@NonNull String fileId, @NonNull GetFileCallback callback) {

    }

    @Override
    public long saveFile(FileEntry file) {
        return Orm.getLiteOrm().insert(file, ConflictAlgorithm.Replace);
    }

    @Override
    public void saveFiles(final List<FileEntry> files) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Orm.getLiteOrm().insert(files, ConflictAlgorithm.Replace);
            }
        });
    }
}
