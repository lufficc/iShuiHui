package com.lufficc.ishuhui.data.source.file;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.lufficc.ishuhui.model.FileEntry;

import java.util.List;

/**
 * Created by lufficc on 2016/11/5.
 */

public interface FilesDataSource {
    interface LoadFilesCallback {
        void onFileLoaded(List<FileEntry> files);

        void onLoadedFailed();
    }1

    interface GetFileCallback {

        void onFileLoaded(FileEntry file);

        void onLoadedFailed();
    }

    void refresh(String chapterId);

    @WorkerThread
    List<FileEntry> getFiles(String chapterId);

    void getFiles(String chapterId, @NonNull LoadFilesCallback callback);

    void getFile(@NonNull String fileId, @NonNull GetFileCallback callback);

    long saveFile(FileEntry file);

    void saveFiles(List<FileEntry> files);

}
