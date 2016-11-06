package com.lufficc.ishuhui.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lufficc.ishuhui.data.source.file.FilesRepository;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.List;

public class DownloadService extends IntentService {
    private static final String ACTION_DOWNLOAD_IMAGES = "com.lufficc.ishuhui.service.action.DOWNLOAD_IMAGES";

    private static final String EXTRA_CHAPTER = "com.lufficc.ishuhui.service.extra.CHAPTER";

    public DownloadService() {
        super("Ishuhui DownloadService");
    }


    public static void startActionDownload(Context context, Chapter chapter) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_IMAGES);
        intent.putExtra(EXTRA_CHAPTER, chapter);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_IMAGES.equals(action)) {
                final Chapter chapter = (Chapter) intent.getSerializableExtra(EXTRA_CHAPTER);
                handleDownload(chapter);
            }
        }
    }


    private void handleDownload(Chapter chapter) {
        List<FileEntry> fileEntries = FilesRepository.getInstance().getFiles(chapter.Id);
        for (int i = 0; i < fileEntries.size(); i++) {
            FileEntry fileEntry = fileEntries.get(i);

        }
        Log.i("download",Thread.currentThread().getName());
    }

}
