package me.jingbin.richeditor.bottomlayout;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.FloatRange;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * 工具类
 * Created by jingbin on 2017/4/6.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class Utils {

    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * @param color 处理的颜色
     * @param i 处理的步长
     * @return  处理完后较深的颜色
     */
    public static int getDarkerColor(int color , @FloatRange(from = 0,to = 1) float i){
        float redrate = 0.299f;
        float greenrate = 0.587f;
        float bluerate = 0.114f;

        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return Color.rgb((int) Math.max(red - redrate * i * 0xFF,0),
                (int) Math.max(green - greenrate * i * 0xFF,0),
                (int) Math.max(blue - bluerate * i *0xFF , 0));
    }


}
