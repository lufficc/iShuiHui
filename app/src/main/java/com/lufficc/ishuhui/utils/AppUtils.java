package com.lufficc.ishuhui.utils;

/**
 * Created by lufficc on 2016/11/5.
 */

public class AppUtils {
    public static String getChapterUrl(String chapterId) {
        return "http://www.ishuhui.net/ComicBooks/ReadComicBooksToIsoV1/" + chapterId + ".html";
    }

    public static void downloadChapterImages(String chapterId) {
        PtrUtil.getInstance().start().put("chapter_" + chapterId + "_is_downloaded", true).apply();
    }

    public static boolean isChapterImagesDownloaded(String chapterId) {
        return PtrUtil.getInstance().getBoolean("chapter_" + chapterId + "_is_downloaded", false);
    }
}
