package me.jingbin.richeditorview.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import me.jingbin.richeditorview.R;

/**
 * Created by jingbin on 2019/2/15.
 */

public class Tools {

    /**
     * 隐藏软键盘
     *
     * @param activity 要隐藏软键盘的activity
     */
    public static void hideSoftKeyBoard(Activity activity) {

        final View v = activity.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            try {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                Log.w("TAG", e.toString());
            }
        }
    }

    /**
     * 显示两个按钮，取消按钮显示灰色，按钮点击执行事件
     *
     * @param v                  布局
     * @param message            内容信息
     * @param buttonPositiveText 右侧按钮文字
     * @param buttonNegativeText 左侧按钮文字
     * @param clickListener      两个按钮的监听事件
     */
    public static void show(View v, String title,String message, String buttonPositiveText, String buttonNegativeText,
                            DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(buttonNegativeText, null);
        builder.setPositiveButton(buttonPositiveText, clickListener);
        builder.show();
//        AlertDialog alertDialog = builder.show();
        // 必须在show 后调用
//        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//        button.setTextColor(CommonUtils.getColor(R.color.color_gray_font));
    }
}
