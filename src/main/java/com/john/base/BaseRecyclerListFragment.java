package com.john.base;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.john.base.baseadapter.BaseAdapterHelper;
import com.john.base.baseadapter.BaseQuickAdapter;
import com.john.base.baseadapter.MultiItemTypeSupport;
import com.john.base.baseadapter.QuickAdapter;
import com.john.base.listener.EndlessRecyclerOnScrollListener;
import com.john.flux.actions.BaseRecyclerListActionCreator;
import com.john.flux.stores.BaseRecyclerListStore;
import com.john.widget.EmptyRecyclerView;
import com.john.widget.RecycleViewDivider;

import rx.Observable;


/**
 * Created by oceanzhang on 16/2/17.
 * 包含RecycleView的fragment  support refresh and loadmore
 */
public abstract class  BaseRecyclerListFragment<Entity, Store extends BaseRecyclerListStore<Entity>, Creater extends BaseRecyclerListActionCreator<Entity>> extends BaseFragment<Store,Creater> implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListView;
    private FrameLayout emptyView;
    ProgressBar mProgressBar;
    protected QuickAdapter<Entity> adapter;
    private boolean needLoadData ;
    @Override
    protected boolean flux() {
        return true;
    }
    @Override
    protected int getLayoutId() {
        if (needRefresh()) {
            return R.layout.base_recycler_list;
        } else {
            return R.layout.base_no_refresh_recycler_list;
        }
    }

    protected abstract Observable observable(int page,int pageSize);

    protected abstract int getListItemLayoutId();

    protected void onItemClick(View view, int position) {

    }

    /**
     * 是否加载更多
     * @return
     */
    protected boolean needLoadMore() {
        return false;
    }

    /**
     * 是否允许刷新
     * @return
     */
    protected boolean needRefresh() {
        return true;
    }

    /**
     * 是否添加头部
     * @return
     */
    protected boolean needHeader(){return  false;};

    /**
     * 返回头部的layoutId 仅当needHeader()返回true时才生效
     * @return
     */
    protected @LayoutRes int getHeaderLayoutId(){
        return 0;
    }

    protected int pageSize() {
        return 20;
    }

    protected RecyclerView.LayoutManager layoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void onRefresh() {
        actionsCreator().loadData(observable(1,pageSize()), false);
    }

    protected boolean autoLoadData(){
        return true;
    }

    /**
     * 手动触发刷新数据
     */
    protected void refreshData() {
        startRefresh();
        actionsCreator().loadData(observable(1,pageSize()), false);
    }
    /**
     * 手动触发刷新数据
     */
    protected void refreshData(Observable observable) {
        startRefresh();
        actionsCreator().loadData(observable, false);
    }
    private void startRefresh() {
        if(needRefresh()) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    private void stopRefresh() {
        if(needRefresh()) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void stopLoadMore() {
        mProgressBar.setVisibility(View.GONE);
    }

    protected View getEmptyView(){
        return inflateView(R.layout.view_empty);
    }

    /**
     * bind data with view.
     *
     * @param helper
     * @param item
     */
    protected abstract void convert(BaseAdapterHelper helper, Entity item);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(needHeader() && getHeaderLayoutId() != 0){
            adapter = new QuickAdapter<Entity>(getActivity(),new MyMultiItemTypeSupport(), store().list()) {
                @Override
                protected void convert(BaseAdapterHelper helper, Entity item) {
                    BaseRecyclerListFragment.this.convert(helper, item);
                }
            };
        }else {
            adapter = new QuickAdapter<Entity>(getActivity(), getListItemLayoutId(), store().list()) {
                @Override
                protected void convert(BaseAdapterHelper helper, Entity item) {
                    BaseRecyclerListFragment.this.convert(helper, item);
                }
            };
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BaseRecyclerListFragment.this.onItemClick(view, position);
            }
        });
        needLoadData = true;
    }

    @Override
    public void initView(View view) {
        emptyView = (FrameLayout) view.findViewById(R.id.listview_empty);
        mListView = (RecyclerView) view.findViewById(R.id.listview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (needRefresh()) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                    R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        }
        final RecyclerView.LayoutManager layoutManager = layoutManager();
        if(needHeader() && getHeaderLayoutId() != 0) {
            if (layoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return position == 0 ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                    }
                });
            }
        }
        if(!(layoutManager instanceof GridLayoutManager)){
            mListView.addItemDecoration(new RecycleViewDivider(
                    getActivity(), LinearLayoutManager.HORIZONTAL, dividerHeight(), dividerColor()));
        }
        if(mListView instanceof EmptyRecyclerView){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            emptyView.addView(getEmptyView(),params);
            ((EmptyRecyclerView)mListView).setEmptyView(emptyView);
        }
        mListView.setLayoutManager(layoutManager);
        if (needLoadMore()) {
            mListView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                protected int pageSize() {
                    return BaseRecyclerListFragment.this.pageSize();
                }

                @Override
                protected ProgressBar progressBar() {
                    return BaseRecyclerListFragment.this.mProgressBar;
                }

                @Override
                public void onLoadMore() {
                    mProgressBar.setVisibility(View.VISIBLE);
                    actionsCreator().loadData(observable(store().getCurrentPage() + 1, pageSize()), true);
                }
            });
        }
        mListView.setAdapter(adapter);
    }

    protected int dividerHeight(){
        return 10;
    }

    protected @ColorInt int dividerColor(){
        return getResources().getColor(R.color.main_line);
    }

    @Override
    public void initData() {
        super.initData();
        if(needLoadData && autoLoadData()) {
            startRefresh();
            actionsCreator().loadData(observable(1, pageSize()), false);
            needLoadData = false;
        }
    }

    /**
     * 数据改变 回调刷新view
     *
     * @param event
     */
    @Override
    protected void updateView(com.john.flux.stores.Store.StoreChangeEvent event){
        stopRefresh();
        stopLoadMore();
        if(event.error){
            showToast(event.message);
            return;
        }
        if(needHeader()){
            if(store().list() != null && store().list().size() > 0 && store().list().get(0) != null){
                store().list().add(0,null);
            }
        }
        adapter.notifyDataSetChanged();
    }


    protected void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    private class MyMultiItemTypeSupport implements MultiItemTypeSupport<Entity> {
        static final int HEADER = 0;
        static final int CONTENT = 1;


        @Override
        public int getItemViewType(int position, Object o) {
            if(position == 0){
                return HEADER;
            }else {
                return CONTENT;
            }
        }

        @Override
        public int getLayoutId(int viewType) {
            if(viewType == HEADER){
                return getHeaderLayoutId();
            }else {
                return getListItemLayoutId();
            }
        }
    }
}
