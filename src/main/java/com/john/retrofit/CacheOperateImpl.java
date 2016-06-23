package com.john.retrofit;

import android.content.Context;
import android.util.LruCache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.disklrucache.DiskLruCache;
import com.john.utils.DateUtil;
import com.john.utils.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by oceanzhang on 16/3/9.
 */
public class CacheOperateImpl implements ICacheOperate {
    private DiskLruCache diskCache;
    private LruCache<String, Object> memoryCache;
    public CacheOperateImpl(File diskDirectory, long maxDiskSize, int memoryEntries) {
        try {
            diskCache = DiskLruCache.open(diskDirectory, 1, 1, maxDiskSize);
        } catch (IOException exc) {
            Log.e("CacheOperateImpl", exc.getMessage());
            diskCache = null;
        }

        memoryCache = new LruCache<>(memoryEntries);
    }
    private static final long REASONABLE_DISK_SIZE = 1024 * 1024; // 1 MB
    private static final int REASONABLE_MEM_ENTRIES = 50; // 50 entries
    public static CacheOperateImpl createInstance(Context context) {
        return new CacheOperateImpl(
                new File(context.getCacheDir(), "retrofit_cache"),
                REASONABLE_DISK_SIZE,
                REASONABLE_MEM_ENTRIES);
    }
    @Override
    public void put(String url, Buffer buffer, long time) throws IOException {
        byte[] rawResponse = buffer.readByteArray();
        String cacheKey = MD5.getMD5(url);
        memoryCache.put(cacheKey, rawResponse);
        DiskLruCache.Editor editor = diskCache.edit(cacheKey);
        JsonObject object = new JsonObject();
        object.addProperty("data",new String(rawResponse, Charset.defaultCharset()));
        object.addProperty("time", time);
        editor.set(0, object.toString());
        editor.commit();
    }

    @Override
    public ResponseBody get(String url,CacheType cacheType) throws IOException {
        String cacheKey = MD5.getMD5(url);
        byte[] memoryResponse = (byte[]) memoryCache.get(cacheKey);
        if (memoryResponse != null) {
            Log.d("CacheOperateImpl", "Memory hit!");
            return ResponseBody.create(null, memoryResponse);
        }
        DiskLruCache.Snapshot cacheSnapshot = diskCache.get(cacheKey);
        if (cacheSnapshot != null) {
            Log.d("CacheOperateImpl", "Disk hit!");
            boolean expired = false;
            JsonElement jelement = new JsonParser().parse(cacheSnapshot.getString(0));
            JsonObject jobject = jelement.getAsJsonObject();
            byte[] bytes = jobject.get("data").getAsString().getBytes();
            if(cacheType == CacheType.NORMAL || cacheType == CacheType.HOURLY || cacheType == CacheType.DAILY){
                long time = jobject.get("time").getAsLong();
                long now = System.currentTimeMillis();
                long dt = now - time;
                long today = DateUtil.today(now);
                if (cacheType == CacheType.NORMAL) {
                    expired = dt < 0 || dt > 300000;
                } else if (cacheType == CacheType.HOURLY) {
                    expired = dt < 0 || dt > 3600000;
                } else {
                    expired = dt < 0 || time < today;
                }
                if (!expired) {
                    return ResponseBody.create(null, bytes);
                }
            }else if(cacheType == CacheType.CRITICAL){
                return ResponseBody.create(null, bytes);
            }

        }
        throw new IOException("cache is null.");
    }
}
