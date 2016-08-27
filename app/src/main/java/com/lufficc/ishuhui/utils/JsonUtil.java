package com.lufficc.ishuhui.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by lcc_luffy on 2016/1/25.
 */
public class JsonUtil {
    private static Gson gson;

    private JsonUtil() {
    }

    public static Gson getInstance() {
        if (gson == null) {
            synchronized (JsonUtil.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    public static String toJson(Object src) {
        return getInstance().toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return getInstance().fromJson(json, classOfT);
    }

}
