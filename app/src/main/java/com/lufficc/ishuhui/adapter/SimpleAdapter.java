package com.lufficc.ishuhui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by lcc_luffy on 2016/3/22.
 */
public abstract class SimpleAdapter<Holder extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<Holder> {

    protected final List<T> data;
    protected OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public T getData(int position) {
        return data.get(position);
    }

    public SimpleAdapter() {
        data = new ArrayList<>();
    }

    public void setData(Collection<T> otherData) {
        data.clear();
        if (!(otherData == null || otherData.isEmpty())) {
            data.addAll(otherData);
        }
        notifyDataSetChanged();
    }


    public boolean isDataEmpty() {
        return data.isEmpty();
    }

    @SafeVarargs
    public final void setData(T... otherData) {
        data.clear();
        if (!(otherData == null || otherData.length == 0)) {
            Collections.addAll(data, otherData);
        }
        notifyDataSetChanged();
    }


    public List<T> getData() {
        return data;
    }

    public void addData(Collection<T> otherData) {
        if (otherData == null || otherData.isEmpty())
            return;
        if (data.isEmpty()) {
            setData(otherData);
            return;
        }
        int s = data.size();
        data.addAll(otherData);
        notifyItemRangeInserted(s, otherData.size());
    }


    public void clearData()
    {
        data.clear();
        notifyDataSetChanged();
    }


    public void remove(int location) {
        if (location >= 0 && location < data.size()) {
            data.remove(location);
            notifyItemRemoved(location);
        } else {
            Log.i("main", "remove data out of data bounds");
        }
    }

    public void insert(int location, T otherData) {
        data.add(location, otherData);
        notifyItemInserted(location);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
