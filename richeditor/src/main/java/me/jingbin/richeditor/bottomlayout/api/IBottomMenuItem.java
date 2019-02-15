package me.jingbin.richeditor.bottomlayout.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import me.jingbin.richeditor.bottomlayout.logiclist.MenuItem;


/**
 * Created by jingbin on 2017/11/6.
 */

public interface IBottomMenuItem {
    Long getItemId();
    View getMainView();

    interface OnItemClickListenerParcelable extends Parcelable {
        void onItemClick(MenuItem item);

        @Override
        int describeContents();

        @Override
        void writeToParcel(Parcel dest, int flags);
    }

    interface OnBottomItemClickListener{
        boolean onItemClick(MenuItem item, boolean isSelected);
    }

}
