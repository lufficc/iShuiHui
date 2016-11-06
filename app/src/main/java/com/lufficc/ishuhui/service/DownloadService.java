package com.lufficc.ishuhui.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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


    private void handleDownload(Comic comic, Chapter chapter) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        File dir = getChapterDir(comic, chapter);
        if (dir == null) {
            Log.i("handleDownload", "create dir failed");
            return;
        }

        Log.i("handleDownload", " start handleDownload");
        List<FileEntry> fileEntries = FilesRepository.getInstance().getFiles(chapter.Id);
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
                Log.i("handleDownload", "第" + fileEntry.getTitle() + "张 downloaded: download finished.");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        ChapterImages chapterImages = new ChapterImages();
        chapterImages.setChapterId(chapter.Id);
        chapterImages.setChapterName(chapter.Title);
        chapterImages.setComicId(String.valueOf(comic.Id));
        chapterImages.setComicName(comic.Title);
        chapterImages.setImages(fileEntries);
        long id = Orm.getLiteOrm().insert(chapterImages, ConflictAlgorithm.Replace);
        Log.i("handleDownload", chapter.Title + "download finished:" + id);
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
