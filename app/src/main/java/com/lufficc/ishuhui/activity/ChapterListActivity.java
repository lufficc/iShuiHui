package com.lufficc.ishuhui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.adapter.FragmentsAdapter;
import com.lufficc.ishuhui.constants.API;
import com.lufficc.ishuhui.fragment.ChapterListFragment;
import com.lufficc.ishuhui.model.Chapter;
import com.lufficc.ishuhui.model.Comic;
import com.lufficc.ishuhui.utils.JsonUtil;
import com.lufficc.ishuhui.utils.PtrUtil;

import butterknife.BindView;


public class ChapterListActivity extends BaseActivity {
    public static final String COMIC = "COMIC";

    public static final String KEY_LAST_SEE = "KEY_LAST_SEE";

    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private Comic comic;
    private String title;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    private void init() {
        setTitle(title);
        FragmentsAdapter adapter = new FragmentsAdapter(getSupportFragmentManager(), ChapterListFragment.newInstance(comic));
        viewPager.setAdapter(adapter);

    }

    private final static int MENU_ID = 89456;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String json = PtrUtil.getInstance().getString("book" + bookId, null);
        if (json != null) {
            Chapter chapter = JsonUtil.getInstance().fromJson(json, Chapter.class);
            MenuItem menuItem = menu.findItem(MENU_ID);
            if (menuItem == null) {
                menu.add(100, MENU_ID, 100, chapter.Title);
            } else {
                menuItem.setTitle(chapter.Title);
            }
        }
        return true;
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

            case MENU_ID:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chapter_list;
    }
}
