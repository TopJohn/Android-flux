package com.john.retrofit;


import java.io.IOException;

import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by oceanzhang on 16/3/9.
 */
public interface ICacheOperate {
    void put(String url, Buffer buffer, long time) throws IOException;
    ResponseBody get(String url, CacheType cacheType) throws IOException;
}
