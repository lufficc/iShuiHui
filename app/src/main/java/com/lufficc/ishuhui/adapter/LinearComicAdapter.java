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
import com.lufficc.ishuhui.activity.ChapterListActivity;
import com.lufficc.ishuhui.model.Comic;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class LinearComicAdapter extends LoadMoreAdapter<Comic> {

    public LinearComicAdapter(final Context context) {
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ChapterListActivity.showChapterList(context, data.get(position));
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_detial, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_comic_title)
        TextView tv_comic_title;

        @BindView(R.id.tv_comic_intro)
        TextView tv_comic_intro;

        @BindView(R.id.tv_num_last)
        TextView tv_num_last;

        @BindView(R.id.tv_comic_status)
        TextView tv_comic_status;

        @BindView(R.id.iv_zone_item)
        ImageView iv_zone_item;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void onBindData(Comic data) {
            tv_comic_title.setText(data.Title);
            tv_comic_intro.setText(data.Explain);
            tv_num_last.setText(data.LastChapter.Title);
            tv_comic_status.setText(data.Author);
            Glide.with(itemView.getContext())
                    .load(data.FrontCover)
                    .centerCrop()
                    .placeholder(R.color.gray)
                    .into(iv_zone_item);
        }
    }
}
