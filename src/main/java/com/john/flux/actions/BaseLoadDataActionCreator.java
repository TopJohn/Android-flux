package com.john.flux.actions;

import com.john.flux.dispatcher.Dispatcher;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by oceanzhang on 16/3/31.
 */
public class BaseLoadDataActionCreator<T> extends ActionsCreator {
    public BaseLoadDataActionCreator(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public void loadData(Observable<T> observable){
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<T>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dispatcher.dispatch("BaseLoadDataActionCreatorLoadData","error",e.getMessage());
                    }

                    @Override
                    public void onNext(T t) {
                        dispatcher.dispatch("BaseLoadDataActionCreatorLoadData","data",t);
                    }
                });
    }

}
