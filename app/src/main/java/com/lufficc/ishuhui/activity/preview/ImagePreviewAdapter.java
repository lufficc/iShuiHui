package com.lufficc.ishuhui.activity.preview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.FileEntry;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImagePreviewAdapter extends PagerAdapter implements PhotoViewAttacher.OnPhotoTapListener {

    private List<FileEntry> imageItemList;
    private Context context;
    private View currentView;

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }

    private PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener;

    public ImagePreviewAdapter(Context context, @NonNull List<FileEntry> imageItemList) {
        this.imageItemList = imageItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageItemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
    }

    public View getPrimaryItem() {
        return currentView;
    }

    public ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.photoView);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, container, false);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        final PhotoView imageView = (PhotoView) view.findViewById(R.id.photoView);
        final FileEntry imageItem = imageItemList.get(position);
        imageView.setOnPhotoTapListener(this);
        String first = imageItem.getLocalPath() != null ? imageItem.getLocalPath() : imageItem.getUrl();
        loadImage(imageView, progressBar, first, imageItem.getUrl(), true);
        container.addView(view);
        return view;
    }

    private void loadImage(final ImageView imageView, final ProgressBar progressBar, final String first, final String second, final boolean retry) {
        Glide.with(imageView.getContext())
                .load(first)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (retry) {
                            loadImage(imageView, progressBar, second, first, false);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        if (onPhotoTapListener != null)
            onPhotoTapListener.onPhotoTap(view, x, y);
    }
}