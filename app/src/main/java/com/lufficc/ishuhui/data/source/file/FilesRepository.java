package com.lufficc.ishuhui.data.source.file;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lufficc.ishuhui.data.source.file.local.FilesLocalDataSource;
import com.lufficc.ishuhui.data.source.file.remote.FilesRemoteDataSource;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FilesRepository implements FilesDataSource {
    private static FilesRepository INSTANCE;
    private boolean isDirty = false;

    private FilesLocalDataSource localDataSource;
    private FilesRemoteDataSource remoteDataSource;
    private Map<String, List<FileEntry>> fileEntryMap = new LinkedHashMap<>();

    public static FilesRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (FilesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilesRepository();
                }
            }
        }
        return INSTANCE;
    }

    private FilesRepository() {
        localDataSource = FilesLocalDataSource.getInstance();
        remoteDataSource = FilesRemoteDataSource.getInstance();
    }

    @Override
    public void getFiles(final String chapterId, @NonNull final LoadFilesCallback callback) {
        if (!isDirty) {
            List<FileEntry> fileEntries = fileEntryMap.get(chapterId);
            if (fileEntries != null) {
                Log.i("main", "in memory cache");
                callback.onFileLoaded(fileEntries);
                return;
            }
        }
        localDataSource.getFiles(chapterId, new LoadFilesCallback() {
            @Override
            public void onFileLoaded(List<FileEntry> files) {
                Log.i("main", "localDataSource");
                fileEntryMap.put(chapterId, files);
                callback.onFileLoaded(files);
            }

            @Override
            public void onLoadedFailed() {
                remoteDataSource.getFiles(chapterId, new LoadFilesCallback() {
                    @Override
                    public void onFileLoaded(List<FileEntry> files) {
                        fileEntryMap.put(chapterId, files);
                        Log.i("main", "remoteDataSource");
                        callback.onFileLoaded(files);
                    }

                    @Override
                    public void onLoadedFailed() {
                        callback.onLoadedFailed();
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
    public void saveFiles(List<FileEntry> files) {
        localDataSource.saveFiles(files);
    }
}
