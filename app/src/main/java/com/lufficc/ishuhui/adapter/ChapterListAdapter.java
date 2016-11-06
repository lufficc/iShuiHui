package com.lufficc.ishuhui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.lufficc.ishuhui.activity.preview.ImageItem;
import com.lufficc.ishuhui.activity.preview.ImagesActivity;
import com.lufficc.ishuhui.adapter.viewholder.ChapterListViewHolderProvider;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.data.source.file.FilesDataSource;
import com.lufficc.ishuhui.data.source.file.FilesRepository;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.utils.AppUtils;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;
import com.lufficc.lightadapter.OnDataClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListAdapter extends LightAdapter implements FilesDataSource.LoadFilesCallback {
    @Nullable
    private Comic comic;
    private Context context;
    private Chapter currentChapter;
    private ProgressDialog progressDialog;

    public LoadMoreFooterModel getLoadMoreFooterModel() {
        return loadMoreFooterModel;
    }

    private LoadMoreFooterModel loadMoreFooterModel;

    public ChapterListAdapter(final Context context, final @Nullable Comic comic) {
        this.context = context;
        this.comic = comic;
        register(Chapter.class, new ChapterListViewHolderProvider(context, this, comic));
        register(HeaderViewProvider.Header.class, new HeaderViewProvider());
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
        addFooter(loadMoreFooterModel = new LoadMoreFooterModel());
        setOnDataClickListener(new OnDataClickListener() {
            @Override
            public void onDataClick(int position, Object data) {
                final Chapter chapter = (Chapter) data;
                if (!AppUtils.isChapterImagesDownloaded(chapter.Id)) {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("稍等");
                        progressDialog.setMessage("正在加载....");
                    }
                    progressDialog.show();
                }
                currentChapter = chapter;
                FilesRepository.getInstance().getFiles(chapter.Id, ChapterListAdapter.this);
            }
        });
    }

    @Override
    public void onFileLoaded(List<FileEntry> files) {
        final List<ImageItem> list = new ArrayList<>();
        for (FileEntry fileEntry : files) {
            if (comic != null) {
                fileEntry.setComicId(String.valueOf(comic.Id));
                fileEntry.setComicName(comic.Title);
                fileEntry.setChapterName(currentChapter.Title);
            }
            ImageItem imageItem = new ImageItem(fileEntry.getUrl(), fileEntry.getTitle());
            imageItem.setLocalPath(fileEntry.getLocalPath());
            list.add(imageItem);
        }
        if (comic != null) {
            FilesRepository.getInstance().saveFiles(files);
        }
        AppUtils.downloadChapterImages(currentChapter.Id);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (!list.isEmpty()) {
            ImagesActivity.showImages(context, list);
        } else {
            onLoadedFailed();
        }

    }

    @Override
    public void onLoadedFailed() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_SHORT).show();
    }
}
