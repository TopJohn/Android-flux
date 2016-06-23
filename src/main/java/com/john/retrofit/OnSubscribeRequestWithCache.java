package com.john.retrofit;

import com.john.utils.Log;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by oceanzhang on 16/3/9.
 */
public class OnSubscribeRequestWithCache<T> implements Observable.OnSubscribe<T> {

    private final String TAG = "OnSubscribeRequestWithCache:";

    // 读取缓存
    private Observable<T> cacheObservable;
    // 读取网络
    private Observable<T> networkObservable;
    // 网络读取完毕,缓存动作
    private Action1<T> storeCacheAction;
    private CacheType cacheType;

    public OnSubscribeRequestWithCache(Observable<T> cacheObservable, Observable<T> networkObservable, Action1<T> storeCacheAction,CacheType cacheType) {
        this.cacheObservable = cacheObservable;
        this.networkObservable = networkObservable;
        this.storeCacheAction = storeCacheAction;
        this.cacheType = cacheType;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        if (cacheObservable != null && cacheType != CacheType.CRITICAL) {
            cacheObservable.subscribe(new CacheSubscriber(subscriber));
        }else{
            networkObservable.subscribe(new NetworkSubscriber(subscriber));
        }
    }

    class CacheSubscriber extends Subscriber<T>{
        Subscriber<? super T> subscriber;

        public CacheSubscriber(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onCompleted() {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                Log.i(TAG, "cache complete!");
                subscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                if(cacheType != CacheType.CRITICAL) {
                    //如果不是关键缓存  缓存失败后走网络
                    Log.i(TAG,"cache error, get data from network!");
                    networkObservable.subscribe(new NetworkSubscriber(subscriber));
                }else{
                    subscriber.onError(e);
                    Log.i(TAG, "cache error, return!");
                }
            }
        }

        @Override
        public void onNext(T t) {
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                subscriber.onNext(t);
            }
        }
    }
    class NetworkSubscriber extends Subscriber<T>{
        Subscriber<? super T> subscriber;
        T data;
        public NetworkSubscriber(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }
        @Override
        public void onCompleted() {
            if(storeCacheAction != null && data != null){
                storeCacheAction.call(data);
            }
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                Log.i(TAG,"network complete!");
                subscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (subscriber != null && !subscriber.isUnsubscribed()) {
                if(cacheType == CacheType.CRITICAL){
                    Log.i(TAG,"network error,try from cache.");
                    cacheObservable.subscribe(new CacheSubscriber(subscriber));
                }else{
                    Log.i(TAG,"network error,return!");
                    subscriber.onError(e);
                }
            }
        }
        @Override
        public void onNext(T t) {
            this.data = t;
            subscriber.onNext(t);
        }
    }

}
