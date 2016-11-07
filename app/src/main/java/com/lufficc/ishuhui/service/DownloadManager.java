package com.lufficc.ishuhui.service;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.lufficc.ishuhui.data.source.file.FilesRepository;
import com.lufficc.ishuhui.manager.Orm;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lufficc on 2016/11/7.
 */

public class DownloadManager {
    private final LinkedList<ChapterImages> DOWNLOADING_IMAGES = new LinkedList<>();
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

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

    public synchronized LinkedList<ChapterImages> getDownloadingQueue() {
        return DOWNLOADING_IMAGES;
    }

    private ChapterImages first() {
        try {
            return getDownloadingQueue().getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ChapterImages removeFirst() {
        try {
            return getDownloadingQueue().removeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @WorkerThread
    private void beforeDownload(Comic comic, Chapter chapter) {
        List<FileEntry> fileEntries = FilesRepository.getInstance().getFiles(chapter.Id);
        ChapterImages chapterImages = new ChapterImages();
        chapterImages.setChapterId(chapter.Id);
        chapterImages.setChapterName(chapter.Title);
        chapterImages.setChapterNo(chapter.ChapterNo);
        chapterImages.setComicId(String.valueOf(comic.Id));
        chapterImages.setComicName(comic.Title);
        chapterImages.setImages(fileEntries);
        long id = Orm.getLiteOrm().cascade().insert(chapterImages, ConflictAlgorithm.Replace);
        Log.i("handleDownload", "chapterImages inserted:" + id);
        getDownloadingQueue().addLast(chapterImages);
    }

    @WorkerThread
    void handleDownload(Comic comic, Chapter chapter) {
        File dir = getChapterDir(comic, chapter);
        if (dir == null) {
            Log.i("handleDownload", "create dir failed");
            return;
        }
        beforeDownload(comic, chapter);
        ChapterImages chapterImages = first();
        while (chapterImages != null) {
            realDownload(dir, chapterImages);
            synchronized (this) {
                removeFirst();
                chapterImages = first();
            }
        }
    }


    @WorkerThread
    private void realDownload(File dir, ChapterImages chapterImages) {
        List<FileEntry> fileEntries = chapterImages.getImages();
        for (FileEntry fileEntry : fileEntries) {
            if (fileEntry.getLocalPath() != null && new File(fileEntry.getLocalPath()).exists()) {
                Log.i("handleDownload", fileEntry.getTitle() + " already exits");
                continue;
            }
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getChapterDir(Comic comic, Chapter chapter) {
        File sdCardRoot = AppUtils.getAppDir();
        File chapterDir = new File(sdCardRoot, File.separator + comic.Title + File.separator + (chapter.ChapterNo + "-" + chapter.Title) + File.separator);
        if (!chapterDir.exists()) {
            if (!chapterDir.mkdirs()) {
                return null;
            }
        }
        return chapterDir;
    }
}
