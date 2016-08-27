package com.lufficc.ishuhui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lufficc.ishuhui.R;

import java.util.Collection;



/**
 * Created by lcc_luffy on 2016/5/22.
 */
public abstract class LoadMoreAdapter<T> extends SimpleAdapter<RecyclerView.ViewHolder, T> {
    public static final int ITEM_TYPE_FOOTER = 0;
    public static final int ITEM_TYPE_HEADER = 2;
    public static final int ITEM_TYPE_NORMAL = 1;

    private int state = FooterViewHolder.STATE_LOAD_MORE;

    protected LayoutInflater inflater;

    private LoadMoreListener loadMoreListener;
    private FooterViewHolder footerViewHolder;

    private View headerView;

    private String loadMoreMsg = "Loading";
    private String noMoreMsg = " - - End - - ";

    public LoadMoreAdapter() {
        registerAdapterDataObserver(new DataObserver());
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (footerViewHolder != null) {
                if (isDataEmpty())
                    footerViewHolder.hide();
                else
                    footerViewHolder.show();
            }
        }
    }

    private boolean footerFullSpan = true;

    public void setFooterFullSpan(boolean footerFullSpan) {
        this.footerFullSpan = footerFullSpan;
    }


    public GridLayoutManager.SpanSizeLookup spanSizeLookup(int span) {
        return new AdapterSpanSizeLookup(span);
    }

    private class AdapterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        AdapterSpanSizeLookup(int span) {
            this.span = span;
        }

        private int span;

        @Override
        public int getSpanSize(int position) {
            if (hasHeaderView()) {
                return ((position == data.size() + 1) || position == 0) ? span : 1;
            }
            return position == data.size() ? span : 1;
        }
    }

    public boolean hasHeaderView() {
        return headerView != null;
    }

    @Override
    public int getItemCount() {
        return data.size() + (hasHeaderView() ? 2 : 1);
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_FOOTER) {
            footerViewHolder.onBind();
        } else if (getItemViewType(position) == ITEM_TYPE_NORMAL) {
            onBindHolder(holder, position - (hasHeaderView() ? 1 : 0));
        }
    }

    public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
    }

    public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ITEM_TYPE_FOOTER == viewType) {
            if (footerViewHolder == null) {
                if (inflater == null)
                    inflater = LayoutInflater.from(parent.getContext());
                View footer = inflater.inflate(R.layout.item_footer_load_more, parent, false);
                if (footerFullSpan) {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.
                            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setFullSpan(true);
                    footer.setLayoutParams(layoutParams);
                }
                footerViewHolder = new FooterViewHolder(footer);

                footerViewHolder.hide();
            }
            return footerViewHolder;
        } else if (ITEM_TYPE_HEADER == viewType && headerView != null) {
            return new EmptyViewHolder(headerView);
        }
        return onCreateHolder(parent, viewType);
    }

    public void setHeaderView(View headerView) {
        if (this.headerView != headerView)
            notifyDataSetChanged();
        this.headerView = headerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeaderView()) {
            if (position == data.size() + 1) {
                return ITEM_TYPE_FOOTER;
            } else if (position == 0) {
                return ITEM_TYPE_HEADER;
            }
            return ITEM_TYPE_NORMAL;
        } else {
            if (position == data.size()) {
                return ITEM_TYPE_FOOTER;
            }
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public void addData(Collection<T> otherData) {
        if (otherData == null || otherData.isEmpty())
            return;
        if (data.isEmpty()) {
            setData(otherData);
            return;
        }
        int s = data.size();
        data.addAll(otherData);
        notifyItemRangeInserted(hasHeaderView() ? s + 1 : s, otherData.size());
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        static final int STATE_LOAD_MORE = 0;
        static final int STATE_NO_MORE = 1;
        static final int STATE_GONE = 2;

        TextView footerText;

        ProgressBar footerProgressBar;


        FooterViewHolder(View itemView) {
            super(itemView);
            footerText = (TextView) itemView.findViewById(R.id.footerText);
            footerProgressBar = (ProgressBar) itemView.findViewById(R.id.footerProgressBar);
        }

        void onBind() {
            switch (state) {
                case STATE_LOAD_MORE:
                    loadMore();
                    if (loadMoreListener != null) {
                        loadMoreListener.onLoadMore();
                    }
                    break;
                case STATE_NO_MORE:
                    noMore();
                    break;
                case STATE_GONE:
                    hide();
                    break;
            }
        }

        private void loadMore() {
            show();
            if (footerProgressBar.getVisibility() != View.VISIBLE) {
                footerProgressBar.setVisibility(View.VISIBLE);
            }
            footerText.setText(loadMoreMsg);
        }

        private void noMore() {
            show();
            if (footerProgressBar.getVisibility() != View.GONE) {
                footerProgressBar.setVisibility(View.GONE);
            }
            footerText.setText(noMoreMsg);
        }

        private void hide() {
            if (itemView.getVisibility() != View.GONE) {
                itemView.setVisibility(View.GONE);
            }
        }

        private void show() {
            if (itemView.getVisibility() != View.VISIBLE) {
                itemView.setVisibility(View.VISIBLE);
            }
        }
    }


    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * tell tha adapter there will not be data ,so the progress will hide,and the load more callback will not be invoked.
     */
    public void noMoreData() {
        state = FooterViewHolder.STATE_NO_MORE;
        if (footerViewHolder != null)
            footerViewHolder.noMore();
    }

    /**
     * @param noMoreMsg show when no more data
     *                  tell tha adapter there will not be data ,so the progress will hide,and the load more callback will not be invoked.
     */
    public void noMoreData(String noMoreMsg) {
        state = FooterViewHolder.STATE_NO_MORE;
        this.noMoreMsg = noMoreMsg;
        if (footerViewHolder != null)
            footerViewHolder.noMore();
    }


    /**
     * tell tha adapter there will be more data ,so the progress will show,and the load more callback will be invoked.
     */
    public void canLoadMore() {
        state = FooterViewHolder.STATE_LOAD_MORE;
        if (footerViewHolder != null)
            footerViewHolder.loadMore();
    }

    /**
     * tell tha adapter there will be more data ,so the progress will show,and the load more callback will be invoked.
     */
    public void canLoadMore(String loadMoreMsg) {
        state = FooterViewHolder.STATE_LOAD_MORE;
        this.loadMoreMsg = loadMoreMsg;
        if (footerViewHolder != null)
            footerViewHolder.loadMore();
    }


    /**
     * set visibility gone
     */
    public void hideFooter() {
        state = FooterViewHolder.STATE_GONE;
        if (footerViewHolder != null)
            footerViewHolder.hide();
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

}
