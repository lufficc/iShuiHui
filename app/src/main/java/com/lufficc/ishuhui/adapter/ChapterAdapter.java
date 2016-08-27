package com.lufficc.ishuhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class ChapterAdapter extends LoadMoreAdapter<Chapter> {

    private final OnItemClickListener onItemClickListener;

    public ChapterAdapter(final Context context) {
        onItemClickListener = new OnItemClickListener() {
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
        };
        setOnItemClickListener(onItemClickListener);
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false));
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chapter_icon)
        ImageView chapter_icon;

        @BindView(R.id.chapter_name)
        TextView chapter_name;

        @BindView(R.id.chapter_date)
        TextView chapter_date;

        @BindView(R.id.chapter_number)
        TextView chapter_number;

        @BindView(R.id.chapter_view)
        Button chapter_view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindData(Chapter data) {
            chapter_name.setText(data.Title);
            chapter_number.setText(data.Sort + "话");
            chapter_date.setText(data.RefreshTimeStr);
            Glide.with(itemView.getContext())
                    .load(data.FrontCover)
                    .centerCrop()
                    .placeholder(R.color.gray)
                    .into(chapter_icon);
            chapter_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
