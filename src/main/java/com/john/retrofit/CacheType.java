package com.john.retrofit;

/**
 * Created by oceanzhang on 16/3/9.
 */
public enum CacheType {
    /**
     * 禁用
     */
    DISABLED,

    /**
     * 标准缓存，由过期时间控制（一般为5分钟）
     */
    NORMAL,

    /**
     * 持续时间为1小时的缓存，和NORMAL类似
     */
    HOURLY,

    /**
     * 持续时间为当天，每天0:00:00过期
     */
    DAILY,

    /**
     * 关键缓存，先尝试网络请求，如果失败，则返回缓存
     */
    CRITICAL
}
