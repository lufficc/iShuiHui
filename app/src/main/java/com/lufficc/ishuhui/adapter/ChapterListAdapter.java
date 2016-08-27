package com.lufficc.ishuhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.WebActivity;
import com.lufficc.ishuhui.manager.ChapterListManager;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListAdapter extends LoadMoreAdapter<Chapter> {

    public ChapterListAdapter(final Context context) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Chapter chapter = data.get(position);
                ChapterListManager.instance().setChapters(data, position);
                PtrUtil.getInstance().start()
                        .put("book" + chapter.BookId, JsonUtil.getInstance().toJson(chapter))
                        .put("book_chapter_" + chapter.BookId, chapter.ChapterNo)
                        .commit();
                WebActivity.showWebView(context, chapter);
            }
        });
    }

    @Override
    public void onBindHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder) holder).onBindData(data.get(position));
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detial_book, parent, false));
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_zone_item)
        ImageView iv_zone_item;

        @BindView(R.id.tv_comic_title)
        TextView tv_comic_title;

        @BindView(R.id.tv_comic_intro)
        TextView tv_comic_intro;

        @BindView(R.id.tv_comic_status)
        TextView tv_comic_status;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBindData(Chapter data) {
            tv_comic_title.setText(data.Title);
            tv_comic_intro.setText(data.RefreshTimeStr);
            tv_comic_status.setText(data.Sort + "话");
            Glide.with(itemView.getContext())
                    .load(data.FrontCover)
                    .centerCrop()
                    .placeholder(R.color.gray)
                    .into(iv_zone_item);
        }
    }
}
