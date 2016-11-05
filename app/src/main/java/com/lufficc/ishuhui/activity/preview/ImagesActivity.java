package com.lufficc.ishuhui.activity.preview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lufficc.ishuhui.R;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagesActivity extends Activity implements ViewTreeObserver.OnPreDrawListener, PhotoViewAttacher.OnPhotoTapListener {
    public static final String IMAGES_DATA = "IMAGES_DATA";
    public static final String CURRENT_IMAGE = "CURRENT_IMAGE";
    private RelativeLayout rootView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private TextView tv_pager;
    private ViewPager viewPager;
    private List<ImageItem> imageItemList;
    private int currentImage;
    private int imageHeight;
    private int imageWidth;
    private int screenWidth;
    private int screenHeight;

    public static void showImages(Context context, List<ImageItem> imageItemList) {
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
        rootView = (RelativeLayout) findViewById(R.id.rootView);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
        Intent intent = getIntent();
        imageItemList = (List<ImageItem>) intent.getSerializableExtra(IMAGES_DATA);
        currentImage = intent.getIntExtra(CURRENT_IMAGE, 0);

        imagePreviewAdapter = new ImagePreviewAdapter(this, imageItemList);
        imagePreviewAdapter.setOnPhotoTapListener(this);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.setCurrentItem(currentImage);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentImage = position;
                setCurrentPage();
            }
        });
        setCurrentPage();
    }

    private void setCurrentPage() {
        tv_pager.setText(String.format(Locale.CHINA, "%1d/%2d", currentImage + 1, imageItemList.size()));
    }

    @Override
    public boolean onPreDraw() {
        rootView.setBackgroundColor(Color.BLACK);
        return true;
    }

    /**
     * 计算图片的宽高
     */
    private void computeImageWidthAndHeight(ImageView imageView) {

        // 获取真实大小
        Drawable drawable = imageView.getDrawable();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
        float h = screenHeight * 1.0f / intrinsicHeight;
        float w = screenWidth * 1.0f / intrinsicWidth;
        if (h > w) h = w;
        else w = h;

        // 得出当宽高至少有一个充满的时候图片对应的宽高
        imageHeight = (int) (intrinsicHeight * h);
        imageWidth = (int) (intrinsicWidth * w);
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {

        if (currentImage < imageItemList.size()) {
            currentImage++;
            viewPager.setCurrentItem(currentImage);
        }

    }
}
