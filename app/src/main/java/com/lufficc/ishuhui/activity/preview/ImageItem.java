package com.lufficc.ishuhui.activity.preview;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/11/5.
 */

public class ImageItem implements Serializable {
    private String url;
    private String title;

    public ImageItem(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public ImageItem() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
