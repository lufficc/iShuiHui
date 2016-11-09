package com.lufficc.ishuhui.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.SparseArray;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.utils.AppUtils;

public class DownloadService extends Service implements DownloadManager.DownLoadListener {
    private static final String ACTION_DOWNLOAD_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_IMAGES";

    private static final String EXTRA_CHAPTER = "com.lufficc.ishuhui.service.extra.CHAPTER";
    private static final String EXTRA_COMIC_NAME = "com.lufficc.ishuhui.service.extra.COMIC";

    //
    private static final String ACTION_DOWNLOAD_CHAPTER_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_CHAPTER_IMAGES";
    private static final String EXTRA_CHAPTER_IMAGES = "com.lufficc.ishuhui.service.extra.CHAPTER_IMAGES";

    //
    private static final String ACTION_DOWNLOAD_LOST = "com.lufficc.ishuhui.service.action.DOWNLOAD_LOST";
    private static final String EXTRA_FILE_ENTRY_LIST = "com.lufficc.ishuhui.service.extra.FILE_ENTRY_LIST";

    @Override
    public void onCreate() {
        super.onCreate();
        managerCompat = NotificationManagerCompat.from(this);
        DownloadManager.getInstance().addDownLoadListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static void startDownload(Context context, String comicName, Chapter chapter) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_IMAGES);
        intent.putExtra(EXTRA_CHAPTER, chapter);
        intent.putExtra(EXTRA_COMIC_NAME, comicName);
        context.startService(intent);
    }

    public static void startDownload(Context context, ChapterImages chapterImages) {
        if (chapterImages == null || chapterImages.getImages() == null || chapterImages.getImages().isEmpty())
            return;
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_CHAPTER_IMAGES);
        intent.putExtra(EXTRA_CHAPTER_IMAGES, chapterImages);
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_IMAGES.equals(action)) {
                Chapter chapter = (Chapter) intent.getSerializableExtra(EXTRA_CHAPTER);
                String comicName = intent.getStringExtra(EXTRA_COMIC_NAME);
                buildNotification(comicName, chapter.Title, Integer.parseInt(chapter.Id));
            } else if (ACTION_DOWNLOAD_CHAPTER_IMAGES.equals(action)) {
                ChapterImages chapterImages = (ChapterImages) intent.getSerializableExtra(EXTRA_CHAPTER_IMAGES);
                buildNotification(chapterImages.getComicName(), chapterImages.getChapterName(), Integer.parseInt(chapterImages.getChapterId()));
            }
        }
        AppUtils.getExecutorService().execute(new DownloadTask(intent));
    }

    private SparseArray<Notification> notificationSparseArray = new SparseArray<>();
    NotificationManagerCompat managerCompat;

    private void buildNotification(String comicName, String chapterName, int id) {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this);
        builder.setSmallIcon(R.mipmap.ic_shuhui);
        builder.setTicker("开始下载" + chapterName);
        builder.setSubText(chapterName + "正在下载");
        builder.setOngoing(true);
        builder.setProgress(100, 1, true);
        builder.setContentText(comicName);
        Notification notification = builder.build();
        managerCompat.notify(id, notification);
        notificationSparseArray.put(id, notification);
    }

    @Override
    public void onDownloadStart(String comicId, String chapterId) {
        int id = Integer.parseInt(chapterId);
        Notification notification = notificationSparseArray.get(id);
        if (notification != null) {
            managerCompat.cancel(id);
        }
    }

    @Override
    public void onChapterDownloaded(String comicId, String chapterId) {

    }

    @Override
    public void onFileDownloaded(FileEntry fileEntry) {
        int id = Integer.parseInt(fileEntry.getChapterId());
        Notification notification = notificationSparseArray.get(id);
        if (notification != null) {

        }
    }

    @Override
    public void onException(FileEntry fileEntry, Exception e) {

    }

    private class DownloadTask implements Runnable {
        private Intent intent;

        DownloadTask(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void run() {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_DOWNLOAD_IMAGES.equals(action)) {
                    Chapter chapter = (Chapter) intent.getSerializableExtra(EXTRA_CHAPTER);
                    String comicName = intent.getStringExtra(EXTRA_COMIC_NAME);
                    DownloadManager.getInstance().handleDownload(comicName, chapter);
                } else if (ACTION_DOWNLOAD_CHAPTER_IMAGES.equals(action)) {
                    ChapterImages chapterImages = (ChapterImages) intent.getSerializableExtra(EXTRA_CHAPTER_IMAGES);
                    DownloadManager.getInstance().handleDownload(chapterImages);
                }
            }
        }
    }
}
