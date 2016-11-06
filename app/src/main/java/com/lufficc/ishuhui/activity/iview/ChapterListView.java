package com.lufficc.ishuhui.activity.iview;

import com.lufficc.ishuhui.model.Chapter;

import java.util.List;

/**
 * Created by lufficc on 2016/8/28.
 */

public interface ChapterListView {
    void onSuccess(List<Chapter> chapters);

    void onFail(Throwable t);
}

