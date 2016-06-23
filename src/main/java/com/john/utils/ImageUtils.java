package com.john.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by oceanzhang on 16/1/24.
 */
public class ImageUtils {

    public static void loadImage(String url, ImageView imageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //TODO set default image to display.
//                .showImageOnLoading(R.drawable.ic_stub)
//                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }
    public static void loadImage(String url, ImageView imageView,int w,int h) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //TODO set default image to display.
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url,new ImageViewAware(imageView),options,new ImageSize(w,h),null,null);

    }

    /**
     * 高度固定 长度按比例缩放
     * @param url
     * @param imageView
     * @param h 固定高度
     */
    public static void loadImageFitHeight(Context context,String url, ImageView imageView,int h) {
        Picasso.with(context)
                .load(url)
                .transform(new MyTransformation(h,MyTransformation.vertical))
                .into(imageView);

    }
    /**
     * 宽度固定 高度按比例缩放
     * @param url
     * @param imageView
     * @param w 固定宽度
     */
    public static void loadImageFitWidth(Context context,String url, ImageView imageView,int w) {
        Picasso.with(context)
                .load(url)
                .transform(new MyTransformation(w,MyTransformation.horizontal))
                .into(imageView);

    }
    public static class MyTransformation implements Transformation {
        public static final int vertical = 0; //高固定
        public static final int horizontal = 1; //宽固定
        int size;
        int orientation;

        public MyTransformation(int size, int orientation) {
            this.size = size;
            this.orientation = orientation;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int w = source.getWidth();
            int h = source.getHeight();
            float scale;
            if(orientation == vertical){
                scale =  (size*1.0f) / h;
            }else{
                scale =  (size*1.0f) / w;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale); //长和宽放大缩小的比例
            Bitmap result = Bitmap.createBitmap(source, 0, 0, w, h, matrix, true);
            if(result != source){
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "chat message image show.";
        }
    }
    public static Bitmap loadImage(String url[]) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //TODO set default image to display.
//                .showImageOnLoading(R.drawable.ic_stub)
//                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return ImageLoader.getInstance().loadImageSync(url[0], options);
    }


    public static void loadImage(String url, ImageLoadingListener listener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //TODO set default image to display.
//                .showImageOnLoading(R.drawable.ic_stub)
//                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().loadImage(url, options, listener);
    }

    public static File getTempImage() throws IOException {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            tempFile.createNewFile();
            return tempFile;
        }
        throw new IOException("cannot find any sdcard.");
    }

    public static File getTempVideo() throws IOException {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.mp4");
            tempFile.createNewFile();
            return tempFile;
        }
        throw new IOException("cannot find any sdcard.");
    }

    public static Bitmap getScaleBitmap(Context ctx, String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

        int bmpWidth = opt.outWidth;
        int bmpHeght = opt.outHeight;

        int screenWidth = ViewUtil.getScreenWidthPixels(ctx);
        int screenHeight = ViewUtil.getScreenHeightPixels(ctx);

        opt.inSampleSize = 1;
        if (bmpWidth > bmpHeght) {
            if (bmpWidth > screenWidth)
                opt.inSampleSize = bmpWidth / screenWidth;
        } else {
            if (bmpHeght > screenHeight)
                opt.inSampleSize = bmpHeght / screenHeight;
        }
        opt.inJustDecodeBounds = false;

        bmp = BitmapFactory.decodeFile(filePath, opt);
        return bmp;
    }

    public static void saveToFile(Bitmap bmp, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fout = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
        fout.close();
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(Context ctx, String videoPath) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        int screenWidth = ViewUtil.getScreenWidthPixels(ctx);
        int screenHeight = ViewUtil.getScreenHeightPixels(ctx);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, screenWidth, screenHeight,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

}
