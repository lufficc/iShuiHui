package com.lufficc.ishuhui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.lufficc.ishuhui.activity.preview.ImagesActivity;
import com.lufficc.ishuhui.adapter.viewholder.ChapterListViewHolderProvider;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.data.source.file.FilesDataSource;
import com.lufficc.ishuhui.data.source.file.FilesRepository;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FileEntry;
import com.lufficc.ishuhui.service.DownloadManager;
import com.lufficc.ishuhui.utils.AppUtils;
import com.lufficc.lightadapter.LightAdapter;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.lightadapter.LoadMoreFooterViewHolderProvider;
import com.lufficc.lightadapter.OnDataClickListener;

import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListAdapter extends LightAdapter implements FilesDataSource.LoadFilesCallback,DownloadManager.DownLoadListener {
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
        for (FileEntry fileEntry : files) {
            if (comic != null) {
                fileEntry.setComicId(String.valueOf(comic.Id));
                fileEntry.setComicName(comic.Title);
                fileEntry.setChapterName(currentChapter.Title);
            }
        }
        if (comic != null) {
            FilesRepository.getInstance().saveFiles(files);
        }
        AppUtils.downloadChapterImages(currentChapter.Id);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (!files.isEmpty()) {
            ImagesActivity.showImages(context, files);
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


    private int chapterId2Index(String comicId, String chapterId) {
        if (Integer.valueOf(comicId) != comic.Id) {
            return -1;
        }
        for (int i = 0; i < getData().size(); i++) {
            Chapter chapter = (Chapter) getData().get(i);
            if (chapter.Id.equals(chapterId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDownloadStart(String comicId, String chapterId) {
        int i = chapterId2Index(comicId, chapterId);
        if (i != -1) {
            updateData(i);
        }
    }

    @Override
    public void onChapterDownloaded(String comicId, String chapterId) {
        int i = chapterId2Index(comicId, chapterId);
        if (i != -1) {
            updateData(i);
        }
    }



    @Override
    public void onFileDownloaded(FileEntry fileEntry) {

    }

    @Override
    public void onException(FileEntry fileEntry, Exception e) {

    }
}
