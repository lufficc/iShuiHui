package com.lufficc.ishuhui.data.source.file;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.lufficc.ishuhui.data.source.file.local.FilesLocalDataSource;
import com.lufficc.ishuhui.data.source.file.remote.FilesRemoteDataSource;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FilesRepository implements FilesDataSource {
    private static FilesRepository INSTANCE;
    private Map<String, Boolean> dirties = new HashMap<>();
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
    public void refresh(String chapterId) {
        dirties.put(chapterId, true);
        fileEntryMap.remove(chapterId);
    }

    @Override
    @WorkerThread
    public List<FileEntry> getFiles(String chapterId) {
        List<FileEntry> fileEntries = localDataSource.getFiles(chapterId);
        if (fileEntries == null || fileEntries.isEmpty()) {
            fileEntries = remoteDataSource.getFiles(chapterId);
        }
        return fileEntries;
    }

    @Override
    public void getFiles(final String chapterId, @NonNull final LoadFilesCallback callback) {
        if (!(dirties.containsKey(chapterId) && dirties.get(chapterId))) {
            List<FileEntry> fileEntries = fileEntryMap.get(chapterId);
            if (fileEntries != null) {
                callback.onFileLoaded(fileEntries);
                return;
            }
        }
        localDataSource.getFiles(chapterId, new LoadFilesCallback() {
            @Override
            public void onFileLoaded(List<FileEntry> files) {
                fileEntryMap.put(chapterId, files);
                callback.onFileLoaded(files);
            }

            @Override
            public void onLoadedFailed() {
                remoteDataSource.getFiles(chapterId, new LoadFilesCallback() {
                    @Override
                    public void onFileLoaded(List<FileEntry> files) {
                        fileEntryMap.put(chapterId, files);
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
    public long saveFile(FileEntry file) {
        return localDataSource.saveFile(file);
    }

    @Override
    public void saveFiles(List<FileEntry> files) {
        localDataSource.saveFiles(files);
    }
}
