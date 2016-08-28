package com.lufficc.ishuhui.activity.iview;

import com.lufficc.ishuhui.model.ChapterListModel;

/**
 * Created by lufficc on 2016/8/28.
 */

public interface ChapterListView {
    void onSuccess(ChapterListModel model);

    void onFail(Throwable t);
}

