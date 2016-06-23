package com.john.base;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by oceanzhang on 16/2/17.
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    protected void init(){
        //image loader init
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(configuration);
//        Fresco.initialize(this);
    }
}
