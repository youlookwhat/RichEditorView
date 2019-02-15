package me.jingbin.richeditor.bottomlayout.menuitem;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;


import java.io.Serializable;

import me.jingbin.richeditor.bottomlayout.api.IBottomMenuItem;
import me.jingbin.richeditor.bottomlayout.api.ITheme;
import me.jingbin.richeditor.bottomlayout.logiclist.MenuItem;

/**
 * 下底栏选项的父类
 * Created by jingbin on 2018/11/6.
 */

@SuppressWarnings("WeakerAccess")
public abstract class AbstractBottomMenuItem<T extends View> implements IBottomMenuItem,Parcelable,Serializable {

    private MenuItem mMenuItem;
    private boolean isSelected = false;
    private transient Context mContext;
    private ITheme mTheme;

    private OnItemClickListener onItemClickListener;

    public AbstractBottomMenuItem(Context context, MenuItem menuItem) {
        mMenuItem = menuItem;
        isSelected = false;
        mContext = context;
    }

    /**
     * 这个函数在视图被添加进父菜单行是执行
     * 对内部View进行创建和设置
     * @hide
     */
    public void onDisplayPrepare(){
        View v = mMenuItem.getContentView();

        if(v == null)
            mMenuItem.setContentView(createView());

        //noinspection unchecked
        settingAfterCreate(isSelected, (T) (mMenuItem.getContentView()));

        mMenuItem.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
    }

    /**
     * 从LuBottomMenu移除时的工作
     */
    public void onViewDestroy(){
        if(getMainView() != null) {
            getMainView().setOnClickListener(null);
            mMenuItem.setContentView(null);
        }
    }

    /**
     * @return 创建的View
     */
    @NonNull
    public abstract T createView();

    /**
     * @param isSelected    是否被选择
     * @param view  要处理的视图
     * 创建视图后的设置工作
     */
    public abstract void settingAfterCreate(boolean isSelected, T view);

    public void onSelectChange(boolean isSelected){
        //do nothing
    }

    /**
     * @return 是否拦截
     *  拦截从LuBottomMenu的监听事件（其分发的点击事件）
     */
    public boolean onItemClickIntercept(){
        return false;
    }

    @Override
    public Long getItemId() {
        return mMenuItem.getId();
    }

    @Override
    public T getMainView() {
        return (T) mMenuItem.getContentView();
    }

    public MenuItem getMenuItem(){
        return mMenuItem;
    }

    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if(onItemClickListener!=this.onItemClickListener)
            this.onItemClickListener = onItemClickListener;
    }

    public final void setSelected(boolean selected) {
        if(selected != isSelected)
            onSelectChange(selected);
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    private void onItemClick(){
        if(!onItemClickIntercept() && onItemClickListener != null)
            onItemClickListener.onItemClick(mMenuItem);
    }

    public ITheme getTheme() {
        return mTheme;
    }

    public void setTheme(ITheme mTheme) {
        if(mTheme != this.mTheme)
            this.mTheme = mTheme;
    }

    public void setThemeForDisplay(ITheme mTheme){
        setTheme(mTheme);
        if(getMainView()!=null){
            settingAfterCreate(isSelected, getMainView());
            getMainView().invalidate();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mMenuItem);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.mTheme, flags);
        dest.writeParcelable(this.onItemClickListener, flags);
    }

    protected AbstractBottomMenuItem(Parcel in) {
        this.mMenuItem = (MenuItem) in.readSerializable();
        this.isSelected = in.readByte() != 0;
        this.mTheme = in.readParcelable(ITheme.class.getClassLoader());
        this.onItemClickListener = in.readParcelable(OnItemClickListener.class.getClassLoader());
    }

    public abstract static class OnItemClickListener implements OnItemClickListenerParcelable{

        @Override
        public abstract void onItemClick(MenuItem item) ;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }

}
