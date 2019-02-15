package me.jingbin.richeditor.bottomlayout.api;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * 自定义主题接口
 * Created by jingbin on 2017/11/17.
 */

public interface ITheme extends Serializable,Parcelable {
    int[] getBackGroundColors();
    int getAccentColor();
    int getNormalColor();
}
