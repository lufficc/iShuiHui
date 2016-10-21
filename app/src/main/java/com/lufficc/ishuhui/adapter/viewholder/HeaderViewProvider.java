package com.lufficc.ishuhui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lufficc.ishuhui.R;
import com.lufficc.lightadapter.ViewHolderProvider;

/**
 * Created by lufficc on 2016/9/5.
 */

public class HeaderViewProvider extends ViewHolderProvider<HeaderViewProvider.Header,HeaderViewProvider.ViewHolder>{
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.header_view, parent, false));
    }

    @Override
    public void onBindViewHolder(Header header, ViewHolder viewHolder,int position) {
        viewHolder.bind(header);
    }

    public static class Header{
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        String title;
        String des;
        String url;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView des;
        ImageView header_image;
        public ViewHolder(View itemView) {
            super(itemView);
            header_image = (ImageView) itemView.findViewById(R.id.header_img);
            des = ((TextView) itemView.findViewById(R.id.header_des));
        }
        void bind(Header header)
        {
            des.setText(header.des);
            Glide.with(itemView.getContext()).load(header.url).centerCrop().into(header_image);
        }
    }
}
