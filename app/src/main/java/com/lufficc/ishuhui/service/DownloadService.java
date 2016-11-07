package com.lufficc.ishuhui.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.Comic;

import java.util.LinkedList;

public class DownloadService extends IntentService {
    private static final String ACTION_DOWNLOAD_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_IMAGES";

    private static final String EXTRA_CHAPTER = "com.lufficc.ishuhui.service.extra.CHAPTER";
    private static final String EXTRA_COMIC = "com.lufficc.ishuhui.service.extra.COMIC";

    public DownloadService() {
        super("Ishuhui DownloadService");
    }


    public static void startActionDownload(Context context, Comic comic, Chapter chapter) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_IMAGES);
        intent.putExtra(EXTRA_CHAPTER, chapter);
        intent.putExtra(EXTRA_COMIC, comic);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("handleDownload", "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_IMAGES.equals(action)) {
                Chapter chapter = (Chapter) intent.getSerializableExtra(EXTRA_CHAPTER);
                Comic comic = (Comic) intent.getSerializableExtra(EXTRA_COMIC);
                handleDownload(comic, chapter);
            }
        }
    }


    @WorkerThread
    private void handleDownload(Comic comic, Chapter chapter) {
        DownloadManager.getInstance().handleDownload(comic, chapter);
    }
}
