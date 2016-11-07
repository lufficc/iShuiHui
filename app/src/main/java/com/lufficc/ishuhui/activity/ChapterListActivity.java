package com.lufficc.ishuhui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.activity.iview.SubscribeView;
import com.lufficc.ishuhui.activity.presenter.SubscribePresenter;
import com.lufficc.ishuhui.adapter.FragmentsAdapter;
import com.lufficc.ishuhui.constants.API;
import com.lufficc.ishuhui.fragment.ChapterImagesFragment;
import com.lufficc.ishuhui.fragment.ChapterListFragment;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.model.User;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.ishuhui.utils.SubscribeUtil;

import butterknife.BindView;


public class ChapterListActivity extends BaseActivity implements SubscribeView, View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final String COMIC = "COMIC";

    public static final String KEY_LAST_SEE = "KEY_LAST_SEE";

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    SubscribePresenter subscribePresenter;

    @BindView(R.id.fab_subscribe)
    FloatingActionButton fab_subscribe;

    private Comic comic;
    private String title;
    private int bookId;
    private boolean isSubscribed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comic = (Comic) getIntent().getSerializableExtra(COMIC);
        bookId = comic.Id;
        title = comic.Title;
        isSubscribed = SubscribeUtil.isSubscribed(bookId);
        if (isSubscribed) {
            fab_subscribe.setImageResource(R.mipmap.ic_done);
        }
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


    private void init() {
        setTitle(title);
        subscribePresenter = new SubscribePresenter(this);
        fab_subscribe.setOnClickListener(this);
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager()
                , ChapterListFragment.newInstance(comic)
                , ChapterImagesFragment.newInstance(comic)
        );
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chapter_list;
    }

    private void subscribe() {
        if (!User.getInstance().isLogin()) {
            LoginActivity.login(this);
            toast("登陆后才能订阅吆");
            return;
        }
        subscribePresenter.subscribe(comic, isSubscribed);
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
    public void onDestroy() {
        super.onDestroy();
        subscribePresenter.onDestroy();
    }

    @Override
    public void onSubscribe(boolean isSubscribed) {
        if (isSubscribed) {
            toast("订阅成功");
        } else {
            toast("已取消订阅");
        }
        this.isSubscribed = isSubscribed;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(viewPager.getAdapter().getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
