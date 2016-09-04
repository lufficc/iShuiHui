package com.lufficc.ishuhui.adapter;

import android.content.Context;

import com.lufficc.ishuhui.activity.WebActivity;
import com.lufficc.ishuhui.adapter.viewholder.ChapterListViewHolderProvider;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.manager.ChapterListManager;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;
import com.lufficc.lightadapter.OnDataClickListener;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListAdapter extends LightAdapter {
    public LoadMoreFooterModel getLoadMoreFooterModel() {
        return loadMoreFooterModel;
    }
    private LoadMoreFooterModel loadMoreFooterModel;
    public ChapterListAdapter(final Context context) {
        register(Chapter.class, new ChapterListViewHolderProvider(this));
        register(HeaderViewProvider.Header.class, new HeaderViewProvider());
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
        addFooter(loadMoreFooterModel = new LoadMoreFooterModel());
        setOnDataClickListener(new OnDataClickListener() {
            @Override
            public void onDataClick(int position, Object data) {
                Chapter chapter = (Chapter) data;
                ChapterListManager.instance().setChapters(getData(), position);
                PtrUtil.getInstance().start()
                        .put("book" + chapter.BookId, JsonUtil.getInstance().toJson(chapter))
                        .put("book_chapter_" + chapter.BookId, chapter.ChapterNo)
                        .commit();
                WebActivity.showWebView(context, chapter);
            }
        });
    }
}
