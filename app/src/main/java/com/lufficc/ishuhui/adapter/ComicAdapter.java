package com.lufficc.ishuhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.ChapterListActivity;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.widget.TagImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ComicAdapter extends LoadMoreAdapter<Comic> {

    public ComicAdapter(final Context context) {
        setOnItemClickListener(new SimpleAdapter.OnItemClickListener() {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comic, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        Unbinder unbinder;
        public ViewHolder(View view) {
            super(view);
            unbinder = ButterKnife.bind(this, view);
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
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ((ViewHolder)holder).unbinder.unbind();
    }
}
