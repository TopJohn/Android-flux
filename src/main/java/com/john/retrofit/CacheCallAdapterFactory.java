package com.john.retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by oceanzhang on 16/3/9.
 */
public class CacheCallAdapterFactory extends CallAdapter.Factory {
    private ICacheOperate cacheOperate;
    public CacheCallAdapterFactory(ICacheOperate cacheOperate) {
        this.cacheOperate = cacheOperate;
    }

    public static CacheCallAdapterFactory create(ICacheOperate cacheOperate) {
        return new CacheCallAdapterFactory(cacheOperate);
    }
    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if(rawType != Observable.class){
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        CacheType cacheType = CacheType.DISABLED;
        for (Annotation annotation : annotations) {
            if (annotation instanceof UseCache) {
                cacheType = ((UseCache) annotation).value();
                break;
            }
        }
        return new CacheCallAdapter(observableType, annotations, retrofit, cacheType, cacheOperate);
    }

    static final class CacheCallAdapter implements CallAdapter<Observable> {

        private final ICacheOperate cacheOperate;
        private final Type responseType;
        private final Annotation[] annotations;
        private final Retrofit retrofit;
        private final CacheType cacheType;

        public CacheCallAdapter(Type responseType, Annotation[] annotations, Retrofit retrofit, CacheType cacheType, ICacheOperate cacheOperate) {
            this.responseType = responseType;
            this.annotations = annotations;
            this.retrofit = retrofit;
            this.cacheType = cacheType;
            this.cacheOperate = cacheOperate;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public <R> Observable adapt(Call<R> call) {
            final Request request = buildRequestFromCall(call);
            Observable<R> mCacheResultObservable = null;
            if(cacheType != CacheType.DISABLED) {
                mCacheResultObservable = Observable.create(new Observable.OnSubscribe<R>() {
                    @Override
                    public void call(Subscriber<? super R> subscriber) {
                        Converter<ResponseBody, R> responseBodyRConverter = getResponseConverter(retrofit, responseType, annotations);
                        try {
                            R serverResult = getFromCache(cacheType, request, responseBodyRConverter, cacheOperate);
                            if (subscriber.isUnsubscribed()) return;
                            if(serverResult == null)throw new IOException("result is null.");
                            subscriber.onNext(serverResult);
                            subscriber.onCompleted();
                        }catch (IOException e){
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        }

                    }
                });
            }

            Action1<R> mCacheAction = new Action1<R>() {
                @Override
                public void call(R serverResult) {
                    // store cache action
                    if (serverResult != null) {
                        Converter<R, RequestBody> requestConverter = getRequestConverter(retrofit, responseType, annotations);
                        addToCache(request, serverResult, requestConverter, cacheOperate);
                    }
                }
            };
            Observable<R> serverObservable = Observable.create(new CallOnSubscribe<>(call)) //
                    .flatMap(new Func1<Response<R>, Observable<R>>() {
                        @Override
                        public Observable<R> call(Response<R> response) {
                            if (response.isSuccess()) {
                                return Observable.just(response.body());
                            }
                            return Observable.error(new HttpException(response));
                        }
                    });
            return Observable.create(new OnSubscribeRequestWithCache<R>(mCacheResultObservable, serverObservable, mCacheAction,cacheType));
        }

        private Request buildRequestFromCall(Call call) {
            try {
                Field argsField = call.getClass().getDeclaredField("args");
                argsField.setAccessible(true);
                Object[] args = (Object[]) argsField.get(call);

                Field requestFactoryField = call.getClass().getDeclaredField("requestFactory");
                requestFactoryField.setAccessible(true);
                Object requestFactory = requestFactoryField.get(call);
                Method createMethod = requestFactory.getClass().getDeclaredMethod("create", Object[].class);
                createMethod.setAccessible(true);
                return (Request) createMethod.invoke(requestFactory, new Object[]{args});
            } catch (Exception exc) {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        public static <T> Converter<ResponseBody, T> getResponseConverter(Retrofit retrofit, Type dataType, Annotation[] annotations) {
            return retrofit.responseBodyConverter(dataType, annotations);
        }

        @SuppressWarnings("unchecked")
        public static <T> Converter<T, RequestBody> getRequestConverter(Retrofit retrofit, Type dataType, Annotation[] annotations) {
            // 此处获取RequestBodyConverter,是为了将model转成Buffer,以便写入cache
            return retrofit.requestBodyConverter(dataType, new Annotation[0], annotations);
        }

        static final class CallOnSubscribe<T> implements Observable.OnSubscribe<Response<T>> {
            private final Call<T> originalCall;

            private CallOnSubscribe(Call<T> originalCall) {
                this.originalCall = originalCall;
            }
            @Override
            public void call(final Subscriber<? super Response<T>> subscriber) {
                // Since Call is a one-shot type, clone it for each new subscriber.
                final Call<T> call = originalCall.clone();

                // Attempt to cancel the call if it is still in-flight on unsubscription.
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        call.cancel();
                    }
                }));

                if (subscriber.isUnsubscribed()) {
                    return;
                }
                try {
                    Response<T> response = call.execute();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response);
                    }
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(t);
                    }
                    return;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }

        public static <T> T getFromCache(CacheType cacheType,Request request, Converter<ResponseBody, T> converter, ICacheOperate cacheOperate) throws IOException{
            return converter.convert(cacheOperate.get(request.url().toString(),cacheType));
        }

        public static <T> void addToCache(Request request, T data, Converter<T, RequestBody> converter, ICacheOperate cacheOperate) {
            try {
                Buffer buffer = new Buffer();
                RequestBody requestBody = converter.convert(data);
                requestBody.writeTo(buffer);
                cacheOperate.put(request.url().toString(), buffer, System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
