package com.lufficc.ishuhui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.widget.TagImageView;
import com.lufficc.lightadapter.ViewHolderProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lufficc on 2016/9/1.
 */

public class ComicViewHolderProvider extends ViewHolderProvider<Comic,ComicViewHolderProvider.ViewHolder>{
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_comic, parent, false));
    }

    @Override
    public void onBindViewHolder(Comic comic, ViewHolder viewHolder,int position) {
        viewHolder.onBindData(comic);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_title)
        TextView tv_item_title;

        @BindView(R.id.tv_item_explain)
        TextView tv_item_explain;

        @BindView(R.id.iv_tag)
        TagImageView tagImageView;

        @BindView(R.id.tv_item_time)
        TextView tv_item_time;

        @BindView(R.id.tv_item_author)
        TextView tv_item_author;

        @BindView(R.id.title_LastChapter)
        TextView title_LastChapter;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void onBindData(Comic data) {
            title_LastChapter.setText(data.LastChapter.Title);
            tv_item_explain.setText(data.Explain);
            tv_item_time.setText(data.LastChapter.RefreshTimeStr);
            tv_item_title.setText(data.Title);
            tv_item_author.setText(data.Author);
            Glide.with(itemView.getContext())
                    .load(data.LastChapter.FrontCover)
                    .centerCrop()
                    .placeholder(R.color.gray)
                    .into(tagImageView);
            try {
                String updateTime = data.LastChapter.RefreshTime;
                if (updateTime != null) {
                    String l = updateTime.substring(6, updateTime.length() - 2);
                    long date = Long.parseLong(l);
                    if (System.currentTimeMillis() - date < 3_600_000 * 24 * 3) {
                        tagImageView.setTagEnable(true);
                    } else {
                        tagImageView.setTagEnable(false);
                    }
                }
            } catch (Exception e) {
                tagImageView.setTagEnable(false);
            }
        }
    }
}
