package com.lufficc.ishuhui.adapter;

import android.content.Context;

import com.lufficc.ishuhui.adapter.viewholder.ChapterImagesViewHolderProvider;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.lightadapter.LightAdapter;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterImagesAdapter extends LightAdapter{
    private Context context;

    public ChapterImagesAdapter(final Context context) {
        this.context = context;
        register(ChapterImages.class, new ChapterImagesViewHolderProvider(context));
    }
}
