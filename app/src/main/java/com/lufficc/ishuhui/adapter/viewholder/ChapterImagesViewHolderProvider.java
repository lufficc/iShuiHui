package com.lufficc.ishuhui.adapter.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.lightadapter.ViewHolderProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lufficc on 2016/9/5.
 */

public class ChapterImagesViewHolderProvider extends ViewHolderProvider<ChapterImages, ChapterImagesViewHolderProvider.ViewHolder> {

    private Context context;

    public ChapterImagesViewHolderProvider(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_chapter_images, parent, false));
    }

    @Override
    public void onBindViewHolder(ChapterImages chapter, ViewHolder viewHolder, int position) {
        viewHolder.onBindData(chapter);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView iv_image;

        @BindView(R.id.tv_chapterTitle)
        TextView tv_chapterTitle;

        @BindView(R.id.tv_comicTitle)
        TextView tv_comicTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindData(final ChapterImages data) {
            String chapterNo = "第" + data.getChapterNo() + "话";
            SpannableString spannableString = new SpannableString(chapterNo + " - " + data.getChapterName());
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, chapterNo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, chapterNo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_chapterTitle.setText(spannableString);
            tv_comicTitle.setText(data.getComicName());
            List<FileEntry> fileEntries = data.getImages();
            if (fileEntries != null && !fileEntries.isEmpty()) {
                Glide.with(itemView.getContext()).load(fileEntries.get(0).getLocalPath()).placeholder(R.color.black).error(R.color.black).into(iv_image);
            }
        }
    }


}
