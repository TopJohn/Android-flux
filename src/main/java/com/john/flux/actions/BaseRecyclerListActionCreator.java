package com.john.flux.actions;


import com.john.flux.dispatcher.Dispatcher;

import java.util.List;

import rx.Observable;

/**
 * Created by oceanzhang on 16/2/17.
 */
public abstract class BaseRecyclerListActionCreator<Entity> extends ActionsCreator {
    protected BaseRecyclerListActionCreator(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public abstract void loadData(Observable<Entity> observable,boolean loadMore);

    /**
     * 加载数据完成 成功或者失败
     * @param list 失败list == null.
     * @param loadMore
     */
    protected void loadDataComplete(List<Entity> list,boolean loadMore){
        if(list == null){
            loadDataError("加载数据失败.");
            return;
        }
        dispatcher.dispatch(Actions.ACTION_LOAD_COMPLETE, Key.KEY_DATA, list, Key.KEY_IS_LOAD_MORE, loadMore);
    }
    protected void loadDataError(String error){
        dispatcher.dispatch(Actions.ACTION_LOAD_ERROR,Key.KEY_ERROR,error);
    }
    public interface Actions{
        String ACTION_LOAD_COMPLETE = "action_load_data";
        String ACTION_LOAD_ERROR = "action_load_data";
    }
    public interface Key{
        String KEY_DATA = "list_data";
        String KEY_ERROR = "error_message";
        String KEY_IS_LOAD_MORE = "isloadmore";
    }
}
