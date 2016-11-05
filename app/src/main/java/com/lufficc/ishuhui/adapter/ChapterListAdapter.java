package com.lufficc.ishuhui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lufficc.ishuhui.activity.preview.ImageItem;
import com.lufficc.ishuhui.activity.preview.ImagesActivity;
import com.lufficc.ishuhui.adapter.viewholder.ChapterListViewHolderProvider;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.data.source.FilesDataSource;
import com.lufficc.ishuhui.data.source.FilesRepository;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.FileEntry;
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
    private Comic comic;
    private Context context;
    private Chapter currentChapter;

    public LoadMoreFooterModel getLoadMoreFooterModel() {
        return loadMoreFooterModel;
    }

    private LoadMoreFooterModel loadMoreFooterModel;
    Handler handler = new Handler(Looper.getMainLooper());

    public ChapterListAdapter(final Context context, final Comic comic) {
        this.context = context;
        this.comic = comic;
        register(Chapter.class, new ChapterListViewHolderProvider(this));
        register(HeaderViewProvider.Header.class, new HeaderViewProvider());
        register(LoadMoreFooterModel.class, new LoadMoreFooterViewHolderProvider());
        addFooter(loadMoreFooterModel = new LoadMoreFooterModel());


        setOnDataClickListener(new OnDataClickListener() {
            @Override
            public void onDataClick(int position, Object data) {
                final Chapter chapter = (Chapter) data;
                currentChapter = chapter;
                FilesRepository.getInstance(context).getFiles(chapter.Id, ChapterListAdapter.this);
            }
        });
    }

    @Override
    public void onFileLoaded(List<FileEntry> files) {
        final List<ImageItem> list = new ArrayList<>();
        for (FileEntry fileEntry : files) {
            fileEntry.setComicId(String.valueOf(comic.Id));
            fileEntry.setComicName(comic.Title);
            fileEntry.setChapterName(currentChapter.Title);
            list.add(new ImageItem(fileEntry.getUrl(), fileEntry.getTitle()));
        }
        FilesRepository.getInstance(context).saveFiles(files);
        ImagesActivity.showImages(context, list);
    }

    @Override
    public void onLoadedFailed() {

    }
}
