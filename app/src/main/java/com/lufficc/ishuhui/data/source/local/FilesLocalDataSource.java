package com.lufficc.ishuhui.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.lufficc.ishuhui.data.source.FilesDataSource;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lufficc on 2016/11/5.
 */

public class FilesLocalDataSource implements FilesDataSource {
    private FileDbHelper fileDbHelper;
    private static FilesLocalDataSource INSTANCE;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public FilesLocalDataSource(Context context) {
        fileDbHelper = new FileDbHelper(context);
    }

    public static FilesLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (FilesLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilesLocalDataSource(context);
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
                SQLiteDatabase database = fileDbHelper.getReadableDatabase();
                String[] projection = {
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_ENTRY_ID,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_TITLE,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_URL,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_ID,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_NAME,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_ID,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_NAME,
                        FilesPersistenceContract.FileEntry.COLUMN_NAME_LOCAL_PATH,
                };
                String selection = FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_ID + " = ?";
                String[] selectionArgs = {chapterId};
                Cursor cursor = database.query(FilesPersistenceContract.FileEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                final List<FileEntry> fileEntries = new ArrayList<FileEntry>();
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String fileId = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_ENTRY_ID));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_TITLE));
                        String url = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_URL));
                        String chapter_id = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_ID));
                        String chapterName = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_NAME));
                        String comicId = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_ID));
                        String comicName = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_NAME));
                        String localPath = cursor.getString(cursor.getColumnIndexOrThrow(FilesPersistenceContract.FileEntry.COLUMN_NAME_LOCAL_PATH));
                        FileEntry fileEntry = new FileEntry();
                        fileEntry.setFileId(fileId);
                        fileEntry.setTitle(title);
                        fileEntry.setUrl(url);
                        fileEntry.setChapterId(chapter_id);
                        fileEntry.setChapterName(chapterName);
                        fileEntry.setComicId(comicId);
                        fileEntry.setComicName(comicName);
                        fileEntry.setLocalPath(localPath);
                        fileEntries.add(fileEntry);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                database.close();
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
                SQLiteDatabase database = fileDbHelper.getWritableDatabase();
                database.beginTransaction();
                for (FileEntry f : files) {
                    ContentValues c = new ContentValues();
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_TITLE, f.getTitle());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_URL, f.getUrl());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_ID, f.getChapterId());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_CHAPTER_NAME, f.getChapterName());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_ID, f.getComicId());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_COMIC_NAME, f.getComicName());
                    c.put(FilesPersistenceContract.FileEntry.COLUMN_NAME_LOCAL_PATH, f.getLocalPath());
                    database.insert(FilesPersistenceContract.FileEntry.TABLE_NAME, null, c);
                }
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        });
    }
}
