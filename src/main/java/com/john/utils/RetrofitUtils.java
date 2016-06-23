package com.john.utils;

import retrofit2.Retrofit;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class RetrofitUtils {
    private static Retrofit singleton;

    public static <T> T createApi(Class<T> clazz) {
        if (singleton == null) {
            synchronized (RetrofitUtils.class) {
                if (singleton == null) {
                    singleton = new Retrofit.Builder().baseUrl("").build();
                }
            }
        }
        return singleton.create(clazz);
    }
}
