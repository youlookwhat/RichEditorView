package me.jingbin.richeditorview.tools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


/**
 * Created by jingbin on 2019/02/15.
 * https://blog.csdn.net/qq_33169861/article/details/83586075
 * 解决全屏下，键盘遮挡问题，但这不是全屏，所以要解决状态栏和导航栏高度的问题
 * 解决 webview 下方的布局，当键盘弹起时，布局不在键盘上方的问题
 */

public class KeyBoardListener {

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int statusBarHeight;
    private int navigationHeight;


    public static KeyBoardListener getInstance(Activity activity) {
        return new KeyBoardListener(activity);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//            return new KeyBoardListener(activity);
//        } else {
//            return null;
//        }
    }


    private KeyBoardListener(Activity activity) {
        super();
        this.activity = activity;
        statusBarHeight = getStatusBarHeight(activity);
        navigationHeight = getNavigationBarHeight(activity);
    }


    public void init() {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                /** keyboard probably just became hidden
                 * 由于此方法考虑到的是全屏的键盘弹起方案，
                 * 若不是全屏的时候，底部的布局会显示不全，且缺少的高度正是一个状态栏的高度 和 底部导航栏高度，
                 * 所以这里减少一个状态栏的高度，相当于向上移动一个状态栏的高度，这时候刚好会显示完成。
                 * */
                frameLayoutParams.height = usableHeightSansKeyboard - statusBarHeight - navigationHeight;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取底部导航栏 (Navigation Bar) 高度
     */
    public int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
