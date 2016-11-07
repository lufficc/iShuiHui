package com.lufficc.ishuhui.service;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.data.source.file.FilesRepository;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lufficc on 2016/11/7.
 */

public class DownloadManager {
    private final LinkedList<ChapterImages> DOWNLOADING_IMAGES = new LinkedList<>();
    private final OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final ArrayList<DownLoadListener> downLoadListeners = new ArrayList<>();

    private static DownloadManager INSTANCE;

    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    public void addDownLoadListener(DownLoadListener loadListener) {
        if (loadListener != null) {
            synchronized (this) {
                downLoadListeners.add(loadListener);
            }
        }
    }

    public boolean removeDownLoadListener(DownLoadListener loadListener) {
        if (loadListener != null) {
            synchronized (this) {
                return downLoadListeners.remove(loadListener);
            }
        }
        return false;
    }

    public void removeAllListeners() {
        synchronized (this) {
            downLoadListeners.clear();
        }
    }

    public synchronized boolean isChapterDownloading(String chapterId) {
        for (ChapterImages c : downloadingQueue()) {
            if (c.getChapterId().equals(chapterId))
                return true;
        }
        return false;
    }

    public synchronized boolean isFileEntryDownloading(FileEntry f) {
        for (ChapterImages c : downloadingQueue()) {
            if (c.getChapterId().equals(f.getChapterId())) {
                for (FileEntry tmp : c.getImages()) {
                    if (tmp.getUrl().equals(f.getUrl())) {
                        f.setDownloading(true);
                        return tmp.isDownloading();
                    }
                }
                break;
            }

        }
        return false;
    }

    private synchronized LinkedList<ChapterImages> downloadingQueue() {
        return DOWNLOADING_IMAGES;
    }

    private ChapterImages first() {
        try {
            return downloadingQueue().getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ChapterImages removeFirst() {
        try {
            return downloadingQueue().removeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @WorkerThread
    private void beforeDownload(String comicName, Chapter chapter) {
        List<FileEntry> fileEntries = FilesRepository.getInstance().getFiles(chapter.Id);
        ChapterImages chapterImages = new ChapterImages();
        chapterImages.setChapterId(chapter.Id);
        chapterImages.setChapterName(chapter.Title);
        chapterImages.setChapterNo(chapter.ChapterNo);
        chapterImages.setComicId(chapter.BookId);
        chapterImages.setComicName(comicName);
        chapterImages.setImages(fileEntries);
        long id = Orm.getLiteOrm().cascade().insert(chapterImages, ConflictAlgorithm.Replace);
        Log.i("handleDownload", "chapterImages inserted:" + id);
        downloadingQueue().addLast(chapterImages);
    }


    private void postChapterDownloaded(final String comicId, final String chapterId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (DownloadManager.this) {
                    for (DownLoadListener loadListener : downLoadListeners) {
                        loadListener.onChapterDownloaded(comicId, chapterId);
                    }
                }
            }
        });
    }

    private void postDownloadStart(final String comicId, final String chapterId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (DownloadManager.this) {
                    for (DownLoadListener loadListener : downLoadListeners) {
                        loadListener.onDownloadStart(comicId, chapterId);
                    }
                }
            }
        });
    }

    private void postFileEntryDownloaded(final FileEntry fileEntry) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (DownloadManager.this) {
                    for (DownLoadListener loadListener : downLoadListeners) {
                        loadListener.onFileDownloaded(fileEntry);
                    }
                }
            }
        });
    }

    private void postException(final FileEntry fileEntry, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (DownloadManager.this) {
                    for (DownLoadListener loadListener : downLoadListeners) {
                        loadListener.onException(fileEntry, e);
                    }
                }
            }
        });
    }

    @WorkerThread
    void handleDownload(String comicName, Chapter chapter) {
        File dir = FileUtils.getChapterDir(comicName, chapter);
        if (dir == null) {
            Log.i("handleDownload", "create dir failed");
            return;
        }
        beforeDownload(comicName, chapter);
        ChapterImages chapterImages = first();
        while (chapterImages != null) {
            realDownload(dir, chapterImages);
            removeFirst();
            chapterImages = first();
        }
    }

    @WorkerThread
    private boolean realDownload(File dir, ChapterImages chapterImages) {
        /**
         * post msg
         */
        postDownloadStart(chapterImages.getComicId(), chapterImages.getChapterId());

        boolean result = true;
        List<FileEntry> fileEntries = chapterImages.getImages();
        for (FileEntry fileEntry : fileEntries) {
            if (fileEntry.getLocalPath() != null && new File(fileEntry.getLocalPath()).exists()) {
                Log.i("handleDownload", fileEntry.getTitle() + " already exits");
                /**
                 * post msg
                 */
                postFileEntryDownloaded(fileEntry);
                continue;
            }
            fileEntry.setDownloading(true);
            Request request = new Request.Builder().get().url(fileEntry.getUrl()).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                File image = new File(dir, "第" + fileEntry.getTitle() + "张.jpg");

                InputStream inputStream = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(image);
                int len = 0;
                long sum = 0;
                byte[] buf = new byte[4096];
                while ((len = inputStream.read(buf)) != -1) {
                    sum += len;
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                inputStream.close();
                fileEntry.setLocalPath(image.getAbsolutePath());
                long id = Orm.getLiteOrm().insert(fileEntry, ConflictAlgorithm.Replace);
                Log.i("handleDownload", "第" + fileEntry.getTitle() + "张 downloaded: download finished,id=" + id);
                fileEntry.setDownloading(false);
                /**
                 * post msg
                 */
                postFileEntryDownloaded(fileEntry);
            } catch (Exception e) {
                e.printStackTrace();
                /**
                 * post msg
                 */
                postException(fileEntry, e);
                result = false;
            }
        }
        /**
         * post msg
         */
        postChapterDownloaded(chapterImages.getComicId(), chapterImages.getChapterId());
        return result;
    }


    void handleDownload(ChapterImages chapterImages) {
        File dir = FileUtils.getChapterDir(chapterImages.getComicName(), chapterImages.getChapterNo(), chapterImages.getChapterName());
        if (dir == null) {
            Log.i("handleDownload", "create dir failed");
            return;
        }
        realDownload(dir, chapterImages);
    }

    public static class SimpleDownLoadListener implements DownLoadListener {

        @Override
        public void onDownloadStart(String comicId, String chapterId) {

        }

        @Override
        public void onChapterDownloaded(String comicId, String chapterId) {

        }

        @Override
        public void onFileDownloaded(FileEntry fileEntry) {

        }

        @Override
        public void onException(FileEntry fileEntry, Exception e) {

        }
    }

    public interface DownLoadListener {
        void onDownloadStart(String comicId, String chapterId);

        void onChapterDownloaded(String comicId, String chapterId);

        void onFileDownloaded(FileEntry fileEntry);

        void onException(FileEntry fileEntry, Exception e);
    }
}
