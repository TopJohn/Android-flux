package com.john.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.jauker.widget.BadgeView;

/**
 * 常用单位转换的辅助类
 */
public class ViewUtil {
    private ViewUtil() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    private static int screenWidthPixels;
    private static int screenHeightPixels;
    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * @param context
     * @return
     */
    public static int getScreenWidthPixels(Context context) {

        if (context == null) {
            Log.e("Can't get screen size while the activity is null!");
            return 0;
        }

        if (screenWidthPixels > 0) {
            return screenWidthPixels;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidthPixels = dm.widthPixels;
        return screenWidthPixels;
    }

    /**
     * @param context
     * @return
     */
    public static int getScreenHeightPixels(Context context) {
        if (context == null) {
            Log.e("Can't get screen size while the activity is null!");
            return 0;
        }

        if (screenHeightPixels > 0) {
            return screenHeightPixels;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        screenHeightPixels = dm.heightPixels;
        return screenHeightPixels;
    }

    public static BadgeView showViewBadge(Context context,BadgeView badge,View view,int text){
        if(badge == null) {
            badge = new BadgeView(context);
            badge.setHideOnNull(true);
            badge.setTargetView(view);
            badge.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        }
        badge.setBadgeCount(text);
        return badge;
    }
    public static BadgeView showViewBadge(Context context,View view,int text){
        return showViewBadge(context,null,view,text);
    }
}
