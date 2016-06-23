package com.john.flux.stores;

import com.john.flux.actions.BaseRecyclerListActionCreator;
import com.john.flux.annotation.BindAction;
import com.john.flux.dispatcher.Dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class BaseRecyclerListStore<Entity> extends Store {
    protected List<Entity> list;
    protected int mCurrentPage = 1;

    public BaseRecyclerListStore(Dispatcher dispatcher) {
        super(dispatcher);
        list = new ArrayList<>();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public Entity getItem(int position) {
        if (position < 0 || position >= list.size()) return null;
        return list.get(position);
    }

    public List<Entity> list() {
        return list;
    }

    @BindAction(BaseRecyclerListActionCreator.Actions.ACTION_LOAD_COMPLETE)
    public void loadDataComplete(HashMap<String, Object> data) {
        List<Entity> dataList = (List<Entity>) data.get(BaseRecyclerListActionCreator.Key.KEY_DATA);
        if (dataList == null) {
            emitStoreChange(new StoreChangeEvent(true, "load data error!!"));
            return;
        }
        boolean loadMore = (boolean) data.get(BaseRecyclerListActionCreator.Key.KEY_IS_LOAD_MORE);
        if (loadMore) {
            mCurrentPage++;
            list.addAll(dataList);
        } else {
            mCurrentPage = 1;
            list.clear();
            list.addAll(dataList);
        }
        emitStoreChange(new StoreChangeEvent());
    }
    @BindAction(BaseRecyclerListActionCreator.Actions.ACTION_LOAD_ERROR)
    public void loadDataError(HashMap<String,Object> data){
        String error = (String) data.get(BaseRecyclerListActionCreator.Key.KEY_ERROR);
        emitStoreChange(new StoreChangeEvent(true, "load data error!!"));
    }

}
