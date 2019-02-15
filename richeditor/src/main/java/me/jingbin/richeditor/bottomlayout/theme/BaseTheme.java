package me.jingbin.richeditor.bottomlayout.theme;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import static android.graphics.Color.parseColor;

/**
 * 默认的主题配色
 * Created by jingbin on 2018/11/17.
 */

@SuppressWarnings("WeakerAccess")
public class BaseTheme extends AbstractTheme {


    public static final Parcelable.Creator<BaseTheme> CREATOR = new Parcelable.Creator<BaseTheme>() {
        @Override
        public BaseTheme createFromParcel(Parcel in) {
            return new BaseTheme(in);
        }

        @Override
        public BaseTheme[] newArray(int size) {
            return new BaseTheme[size];
        }
    };

    public BaseTheme() {
        super(null);
    }

    protected BaseTheme(Parcel in) {
        super(in);
    }

    @Override
    public int[] getBackGroundColors() {
        return new int[]{};
    }

    @Override
    public int getAccentColor() {
//        return Color.BLACK;
        return parseColor("#FF4081");
    }

    @Override
    public int getNormalColor() {
        return Color.GRAY;
    }
}
