package com.lufficc.ishuhui.data.source.file.local;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.file.FilesDataSource;
import com.lufficc.ishuhui.model.FileEntry;
import com.orm.SugarRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FilesLocalDataSource implements FilesDataSource {
    private static FilesLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

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
    public void getFiles(final String chapterId, @NonNull final LoadFilesCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<FileEntry> fileEntries = SugarRecord.find(FileEntry.class, "chapter_id = ?", chapterId);
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
    public void saveFile(FileEntry file) {

    }

    @Override
    public void saveFiles(final List<FileEntry> files) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SugarRecord.saveInTx(files);
            }
        });
    }
}
