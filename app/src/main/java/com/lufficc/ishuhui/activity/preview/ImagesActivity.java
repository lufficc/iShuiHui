package com.lufficc.ishuhui.activity.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.FileEntry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagesActivity extends Activity implements PhotoViewAttacher.OnPhotoTapListener, ViewTreeObserver.OnPreDrawListener {
    public static final String IMAGES_DATA = "IMAGES_DATA";
    public static final String CURRENT_IMAGE = "CURRENT_IMAGE";


    private View rootView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private TextView tv_pager;
    private ViewPager viewPager;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;

    private List<FileEntry> imageItemList;
    private int currentImage;


    public static void showImages(Context context, List<FileEntry> imageItemList) {
        if (imageItemList == null || imageItemList.isEmpty()) {
            Toast.makeText(context, "图片列表为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(context, ImagesActivity.class);
        i.putExtra(IMAGES_DATA, (Serializable) imageItemList);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_pager = (TextView) findViewById(R.id.tv_pager);
        rootView = findViewById(R.id.rootView);
        danmakuView = (DanmakuView) findViewById(R.id.sv_danmaku);

        initDanmu();

        Intent intent = getIntent();
        imageItemList = (List<FileEntry>) intent.getSerializableExtra(IMAGES_DATA);


        currentImage = intent.getIntExtra(CURRENT_IMAGE, 0);

        imagePreviewAdapter = new ImagePreviewAdapter(this, imageItemList);
        imagePreviewAdapter.setOnPhotoTapListener(this);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
        viewPager.setCurrentItem(currentImage);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentImage = position;
                setCurrentPage();
            }
        });
        setCurrentPage();
        addDanmu();
    }

    private void initDanmu() {
        danmakuContext = DanmakuContext.create();
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.2f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

        danmakuView.prepare(new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        }, danmakuContext);
        danmakuView.showFPS(true);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (danmakuView != null) {
            // dont forget release!
            danmakuView.release();
            danmakuView = null;
        }
    }

    private void addDanmu() {
        Toast.makeText(this, "before", Toast.LENGTH_SHORT).show();
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        danmaku.text = "这是一条弹幕" + System.nanoTime();
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = true;
        danmaku.setTime(danmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f;
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        danmakuView.addDanmaku(danmaku);
        Toast.makeText(this, danmaku.text, Toast.LENGTH_SHORT).show();
    }

    private void setCurrentPage() {
        tv_pager.setText(String.format(Locale.CHINA, "%1d/%2d", currentImage + 1, imageItemList.size()));
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        addDanmu();
        /*if (currentImage < imageItemList.size()) {
            currentImage++;
            viewPager.setCurrentItem(currentImage);
        }*/
    }

    @Override
    public boolean onPreDraw() {
        rootView.setBackgroundColor(Color.BLACK);
        return true;
    }
}
