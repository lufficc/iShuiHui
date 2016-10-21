package com.lufficc.ishuhui.utils;

/**
 * Created by lufficc on 2016/10/21.
 */

public class SubscribeUtil {

    public static void subscribe(int bookId) {
        PtrUtil.getInstance().start().put(key(bookId), true).commit();
    }

    public static boolean isSubscribed(int bookId) {
        return PtrUtil.getInstance().getBoolean(key(bookId), false);
    }

    private static String key(int bookId) {
        return "bookId" + bookId + "isSubscribed";
    }
}
