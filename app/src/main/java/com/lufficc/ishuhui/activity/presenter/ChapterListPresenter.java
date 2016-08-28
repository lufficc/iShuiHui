package com.lufficc.ishuhui.activity.presenter;

import com.lufficc.ishuhui.activity.iview.ChapterListView;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.model.ChapterListModel;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by lufficc on 2016/8/28.
 */

public class ChapterListPresenter {
    private final ChapterListView chapterListView;
    private Call<ChapterListModel> chapterListCall;

    public ChapterListPresenter(ChapterListView chapterListView) {
        this.chapterListView = chapterListView;
    }
    public void getData(int bookId,int PageIndex){
        chapterListCall = RetrofitManager.api().getComicChapters(String.valueOf(bookId), PageIndex);
        chapterListCall.enqueue(new Callback<ChapterListModel>() {
            @Override
            public void onResponse(retrofit2.Call<ChapterListModel> call, retrofit2.Response<ChapterListModel> response) {
                if (response.isSuccessful()) {
                    chapterListView.onSuccess(response.body());
                } else {
                    chapterListView.onFail(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ChapterListModel> call, Throwable t) {
                chapterListView.onFail(t);
            }
        });
    }

    public void onDestroy()
    {
        if(chapterListCall != null && !chapterListCall.isCanceled())
            chapterListCall.cancel();
    }
}
