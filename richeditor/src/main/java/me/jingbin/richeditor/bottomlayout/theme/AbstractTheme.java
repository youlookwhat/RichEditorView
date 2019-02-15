package me.jingbin.richeditor.bottomlayout.theme;

import android.os.Parcel;

import me.jingbin.richeditor.bottomlayout.api.ITheme;


/**
 * 自定义主题接口
 * Created by jingbin on 2018/11/17.
 */

@SuppressWarnings("WeakerAccess")
public abstract class AbstractTheme implements ITheme {
    public static final int LIGHT_THEME = 0x01;
    public static final int DARK_THEME = 0x02;


    @Override
    public abstract int[] getBackGroundColors() ;

    @Override
    public abstract int getAccentColor();

    @Override
    public abstract int getNormalColor();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    protected AbstractTheme(Parcel in) {

    }

}
