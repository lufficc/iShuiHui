package com.lufficc.ishuhui.adapter.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.ChapterImagesAdapter;
import com.lufficc.ishuhui.data.source.chapter.images.ChapterImagesRepository;
import com.lufficc.ishuhui.model.ChapterImages;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.service.DownloadService;
import com.lufficc.ishuhui.utils.FileUtils;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.ViewHolderProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lufficc on 2016/9/5.
 */

public class ChapterImagesViewHolderProvider extends ViewHolderProvider<ChapterImages, ChapterImagesViewHolderProvider.ViewHolder> {

    private Context context;
    private LightAdapter adapter;

    public ChapterImagesViewHolderProvider(ChapterImagesAdapter chapterImagesAdapter, Context context) {
        this.adapter = chapterImagesAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_chapter_images, parent, false));
    }

    @Override
    public void onBindViewHolder(ChapterImages chapter, ViewHolder viewHolder, int position) {
        viewHolder.onBindData(chapter, position);
    }

    private static int oddColor = Color.parseColor("#50000000");
    private static int evenColor = Color.parseColor("#60000000");

    class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.iv_image)
        ImageView iv_image;

        @BindView(R.id.tv_chapterTitle)
        TextView tv_chapterTitle;

        @BindView(R.id.tv_comicTitle)
        TextView tv_comicTitle;

        @BindView(R.id.tv_chapterNo)
        TextView tv_chapterNo;


        @BindView(R.id.foreground)
        View foreground;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindData(final ChapterImages data, int position) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    buildMenu(tv_chapterTitle, data);
                    return true;
                }
            });
            tv_chapterTitle.setText(data.getChapterName());
            tv_comicTitle.setText(data.getComicName());
            tv_chapterNo.setText("第" + data.getChapterNo() + "话");
            if (position % 2 == 0) {
                foreground.setBackgroundColor(evenColor);
            } else {
                foreground.setBackgroundColor(oddColor);
            }
            final List<FileEntry> fileEntries = data.getImages();
            if (fileEntries != null && !fileEntries.isEmpty()) {
                Glide.with(itemView.getContext()).load(fileEntries.get(0).getLocalPath())
                        .placeholder(R.color.black).error(R.color.black)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Glide.with(itemView.getContext()).load(fileEntries.get(0).getUrl())
                                        .placeholder(R.color.black).error(R.color.black).into(iv_image);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).into(iv_image);
            }
        }

        private void buildMenu(TextView anchor, final ChapterImages data) {
            PopupMenu popupMenu = new PopupMenu(context, anchor);
            Menu menu = popupMenu.getMenu();
            menu.add(1, 1, 1, "删除此章节");
            menu.add(1, 2, 1, "删除(包括本地图片)");
            menu.add(1, 3, 1, "下载丢失图片");
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1:
                            delete(data, false);
                            break;
                        case 2:
                            delete(data, true);
                            break;
                        case 3:
                            downloadLostImages(data);
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }

    private void downloadLostImages(ChapterImages data) {
        DownloadService.startDownload(context, data);
        Toast.makeText(context, "下载开始", Toast.LENGTH_SHORT).show();
    }

    private void delete(final ChapterImages data, final boolean includeLocal) {
        String msg = includeLocal ? "确定删除" + data.getChapterName() + "以及本地图片吗?" : "确定删除" + data.getChapterName() + "?";
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ChapterImagesRepository.getInstance().delete(data) > 0) {
                            adapter.removeData(data);
                        }
                        if (includeLocal) {
                            FileUtils.deleteChapterImages(data);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }


}
