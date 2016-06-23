package com.john.flux.stores;

import com.john.flux.annotation.BindAction;
import com.john.flux.dispatcher.Dispatcher;

import java.util.HashMap;

/**
 * Created by oceanzhang on 16/3/31.
 */
public class BaseLoadDataStore<T> extends Store {
    private T data;
    public BaseLoadDataStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @BindAction("BaseLoadDataActionCreatorLoadData")
    public void loadDataComplete(HashMap<String,Object> d){
        if(d.get("error") == null){
            this.data = (T) d.get("data");
            emitStoreChange(new StoreChangeEvent(200,false,"load data complete."));
        }else{
            emitStoreChange(new StoreChangeEvent(200,true, (String) d.get("error")));
        }
    }

    public T getData() {
        return data;
    }
}
