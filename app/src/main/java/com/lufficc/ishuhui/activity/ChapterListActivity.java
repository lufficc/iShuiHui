package com.lufficc.ishuhui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.ChapterListView;
import com.lufficc.ishuhui.activity.iview.SubscribeView;
import com.lufficc.ishuhui.activity.presenter.ChapterListPresenter;
import com.lufficc.ishuhui.activity.presenter.SubscribePresenter;
import com.lufficc.ishuhui.adapter.ChapterListAdapter;
import com.lufficc.ishuhui.adapter.viewholder.HeaderViewProvider;
import com.lufficc.ishuhui.constants.API;
import com.lufficc.ishuhui.manager.ChapterListManager;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.User;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.ishuhui.utils.SubscribeUtil;
import com.lufficc.ishuhui.widget.DefaultItemDecoration;
import com.lufficc.lightadapter.LoadMoreFooterModel;
import com.lufficc.stateLayout.StateLayout;

import java.util.List;

import butterknife.BindView;


public class ChapterListActivity extends BaseActivity implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ChapterListView,
        SubscribeView {
    public static final String COMIC = "COMIC";

    public static final String KEY_LAST_SEE = "KEY_LAST_SEE";

    @BindView(R.id.stateLayout)
    StateLayout stateLayout;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.fab_subscribe)
    FloatingActionButton fab_subscribe;

    ChapterListAdapter adapter;

    LoadMoreFooterModel footerModel;

    ChapterListPresenter chapterListPresenter;
    SubscribePresenter subscribePresenter;

    private Comic comic;
    HeaderViewProvider.Header header;
    private int PageIndex = 0;
    private String title;
    private int bookId;
    private boolean isSubscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateLayout.showProgressView();
        comic = (Comic) getIntent().getSerializableExtra(COMIC);
        bookId = comic.Id;
        title = comic.Title;
        init();
        PtrUtil.getInstance().start()
                .put(KEY_LAST_SEE, JsonUtil.toJson(comic))
                .commit();

    }

    public static void showChapterList(Context context, Comic comic) {
        Intent intent = new Intent(context, ChapterListActivity.class);
        intent.putExtra(ChapterListActivity.COMIC, comic);
        context.startActivity(intent);
    }


    private ChapterListAdapter initAdapter() {
        adapter = new ChapterListAdapter(this,comic);
        adapter.addHeader(header = new HeaderViewProvider.Header());
        header.setTitle(comic.Title);
        header.setDes(comic.Explain);
        header.setUrl(comic.FrontCover);
        footerModel = adapter.getLoadMoreFooterModel();
        footerModel.setLoadMoreListener(new LoadMoreFooterModel.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                ++PageIndex;
                getData();
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        return adapter;
    }



    private void init() {
        setTitle(title);
        chapterListPresenter = new ChapterListPresenter(this);
        subscribePresenter = new SubscribePresenter(this);
        recyclerView.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.divider),
                getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin)
        ));
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        isSubscribed = SubscribeUtil.isSubscribed(bookId);
        if (isSubscribed) {
            fab_subscribe.setImageResource(R.mipmap.ic_done);
        }
        adapter = initAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);


        fab_subscribe.setOnClickListener(this);
        getData();

    }

    private final static int MENU_ID = 89456;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String json = PtrUtil.getInstance().getString("book" + bookId, null);
        if (json != null) {
            Chapter chapter
                    = JsonUtil.getInstance().fromJson(json, Chapter.class);
            MenuItem menuItem = menu.findItem(MENU_ID);
            if (menuItem == null) {
                menu.add(100, MENU_ID, 100, chapter.Title);
            } else {
                menuItem.setTitle(chapter.Title);
            }
        }
        return true;
    }

    private void subscribe() {
        if (!User.getInstance().isLogin()) {
            LoginActivity.login(this);
            toast("登陆后才能订阅吆");
            return;
        }
        subscribePresenter.subscribe(comic, isSubscribed);
    }

    private void getData() {
        if (adapter.isDataEmpty())
            stateLayout.showProgressView();
        chapterListPresenter.getData(bookId, PageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chapterListPresenter.onDestroy();
        subscribePresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chapter_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(title, API.GET_COMIC_BOOK_DATA + "?id=" + bookId));
                toast(title + " 的章节地址已复制");
                return true;
            case R.id.action_share_link:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, API.GET_COMIC_BOOK_DATA + "?id=" + bookId);
                i.setType("text/plain");
                startActivity(i);
                return true;
            case R.id.action_subscribe:
                subscribe();
                break;

            case MENU_ID:
                String json = PtrUtil.getInstance().getString("book" + bookId, null);
                Chapter chapter
                        = JsonUtil.getInstance().fromJson(json, Chapter.class);
                int ChapterNo = PtrUtil.getInstance().getInt("book_chapter_" + chapter.BookId, 0);
                int position = 0;
                for (Object findChapter : adapter.getData()) {
                    if (((Chapter) findChapter).ChapterNo == ChapterNo) {
                        break;
                    }
                    position++;
                }
                ChapterListManager.instance().setChapters(adapter.getData(), position);
                WebActivity.showWebView(this, chapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chapter_list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_subscribe:
                subscribe();
                break;
        }
    }

    @Override
    public void onRefresh() {
        chapterListPresenter.refresh(String.valueOf(bookId));
        footerModel.canLoadMore();
        PageIndex = 0;
        getData();
    }

    @Override
    public void onSuccess(List<Chapter> chapters) {
        if (chapters.isEmpty()) {
            footerModel.noMoreData();
        } else {
            if (PageIndex == 0) {
                adapter.setData(chapters);
            } else {
                adapter.addData(chapters);
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        if (adapter.isDataEmpty()) {
            stateLayout.showEmptyView();
        } else {
            stateLayout.showContentView();
        }
    }

    @Override
    public void onFail(Throwable t) {
        swipeRefreshLayout.setRefreshing(false);
        if (adapter.isDataEmpty()) {
            stateLayout.showErrorView(t.getMessage());
        }
    }

    @Override
    public void onSubscribe(boolean isSubscribed) {
        if (isSubscribed) {
            toast("订阅成功");
        } else {
            toast("已取消订阅");
        }
        ChapterListActivity.this.isSubscribed = isSubscribed;
        if (isSubscribed) {
            fab_subscribe.setImageResource(R.mipmap.ic_done);
        } else {
            fab_subscribe.setImageResource(R.mipmap.ic_add);
        }
    }

    @Override
    public void onFailSubscribe(String msg) {
        toast(msg);
    }
}
