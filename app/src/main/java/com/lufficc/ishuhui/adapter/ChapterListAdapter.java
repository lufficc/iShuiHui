package com.lufficc.ishuhui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lufficc.ishuhui.activity.preview.ImageItem;
import com.lufficc.ishuhui.activity.preview.ImagesActivity;
import com.lufficc.ishuhui.adapter.viewholder.ChapterListViewHolderProvider;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.utils.AppUtils;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;
import com.lufficc.lightadapter.OnDataClickListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListAdapter extends LightAdapter {
    public LoadMoreFooterModel getLoadMoreFooterModel() {
        return loadMoreFooterModel;
    }

    private LoadMoreFooterModel loadMoreFooterModel;
    Handler handler = new Handler(Looper.getMainLooper());

    public ChapterListAdapter(final Context context) {
        register(Chapter.class, new ChapterListViewHolderProvider(this));
        register(HeaderViewProvider.Header.class, new HeaderViewProvider());
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
        addFooter(loadMoreFooterModel = new LoadMoreFooterModel());
        setOnDataClickListener(new OnDataClickListener() {
            @Override
            public void onDataClick(int position, Object data) {
                final Chapter chapter = (Chapter) data;
                final List<ImageItem> list = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Document document = Jsoup.connect(AppUtils.getChapterUrl(chapter.Id)).get();
                            Elements images = document.getElementsByTag("img");
                            int i = 1;
                            for (Element img : images) {
                                String src = img.attr("src");
                                list.add(new ImageItem(src, i + ""));
                                i++;
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ImagesActivity.showImages(context, list);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
}
