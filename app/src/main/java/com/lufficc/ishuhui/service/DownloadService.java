package com.lufficc.ishuhui.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;

public class DownloadService extends IntentService {
    private static final String ACTION_DOWNLOAD_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_IMAGES";

    private static final String EXTRA_CHAPTER = "com.lufficc.ishuhui.service.extra.CHAPTER";
    private static final String EXTRA_COMIC_NAME = "com.lufficc.ishuhui.service.extra.COMIC";

    //
    private static final String ACTION_DOWNLOAD_CHAPTER_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_CHAPTER_IMAGES";
    private static final String EXTRA_CHAPTER_IMAGES = "com.lufficc.ishuhui.service.extra.CHAPTER_IMAGES";

    //
    private static final String ACTION_DOWNLOAD_LOST = "com.lufficc.ishuhui.service.action.DOWNLOAD_LOST";
    private static final String EXTRA_FILE_ENTRY_LIST = "com.lufficc.ishuhui.service.extra.FILE_ENTRY_LIST";

    public DownloadService() {
        super("Ishuhui DownloadService");
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

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("handleDownload", "onHandleIntent");
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
