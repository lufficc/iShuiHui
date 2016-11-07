package com.lufficc.ishuhui.utils;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;

import java.io.File;
import java.util.List;

/**
 * Created by lufficc on 2016/11/7.
 */

public class FileUtils {
    public static File getAppDir() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        return new File(sdCardRoot, File.separator + "鼠绘漫画" + File.separator);
    }

    @Nullable
    public static File getChapterDir(String comicName, int chapterNo, String chapterTitle) {
        File sdCardRoot = FileUtils.getAppDir();
        File chapterDir = new File(sdCardRoot, File.separator + comicName + File.separator + (chapterNo + "-" + chapterTitle) + File.separator);
        if (!chapterDir.exists()) {
            if (!chapterDir.mkdirs()) {
                return null;
            }
        }
        return chapterDir;
    }

    @Nullable
    public static File getChapterDir(String comicName, Chapter chapter) {
        return getChapterDir(comicName, chapter.ChapterNo, chapter.Title);
    }

    public static boolean deleteChapterImages(ChapterImages chapterImages) {
        boolean result = true;
        List<FileEntry> fileEntries = chapterImages.getImages();
        if (fileEntries != null && !fileEntries.isEmpty()) {
            for (FileEntry fileEntry : fileEntries) {
                File img = new File(fileEntry.getLocalPath());
                if (img.exists()) {
                    result = img.delete() && result;
                }
            }
            File dir = new File(fileEntries.get(0).getLocalPath()).getParentFile();
            if (dir.exists()) {
                result = dir.delete() && result;
            }
        }
        return result;
    }
}
