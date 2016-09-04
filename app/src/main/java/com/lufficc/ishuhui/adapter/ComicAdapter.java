package com.lufficc.ishuhui.adapter;

import android.content.Context;

import com.lufficc.ishuhui.activity.ChapterListActivity;
import com.lufficc.ishuhui.adapter.viewholder.ComicViewHolderProvider;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;
import com.lufficc.lightadapter.OnDataClickListener;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ComicAdapter extends LightAdapter {
    private LoadMoreFooterModel loadMoreFooterModel;

    public ComicAdapter(final Context context) {
        register(Comic.class, new ComicViewHolderProvider());
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
        addFooter(loadMoreFooterModel = new LoadMoreFooterModel());
        loadMoreFooterModel.setFullSpan(true);
        loadMoreFooterModel.setNoMoreMsg("加载完成");
        setOnDataClickListener(new OnDataClickListener() {
            @Override
            public void onDataClick(int i, Object o) {
                ChapterListActivity.showChapterList(context, (Comic) o);
            }
        });
    }

    public LoadMoreFooterModel getLoadMoreFooterModel() {
        return loadMoreFooterModel;
    }
}
