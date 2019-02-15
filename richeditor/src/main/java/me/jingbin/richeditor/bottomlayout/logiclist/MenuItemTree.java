package me.jingbin.richeditor.bottomlayout.logiclist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jingbin on 2018/11/6.
 */

public class MenuItemTree implements Parcelable {
    private MenuItem rootItem;

    public MenuItemTree() {
        init();
    }

    private void init(){
        this.rootItem = new MenuItem(null,null,0x1183424701L,null);
        this.rootItem.setDeep(0);
    }

    public void setRoots(MenuItem ... roots){
        rootItem.setNextLevel(Arrays.asList(roots));
    }

    public void addRootItem(MenuItem item){
        if(rootItem.getNextLevel() == null){
            rootItem.setNextLevel(new ArrayList<MenuItem>());
        }
        item.setParent(rootItem);
        rootItem.getNextLevel().add(item);
    }

    public boolean addByParent(MenuItem parent,MenuItem addMenuItem){
        return addByParentId(parent.getId(),addMenuItem);
    }

    public boolean addByParentId(Long id, MenuItem addMenuItem){
        if(id == null) return false;
        MenuItem findItem = rootItem.getMenuItemById(id);
        if(findItem != null){
            if(findItem.getNextLevel() == null){
                findItem.setNextLevel(new ArrayList<MenuItem>());
            }
            addMenuItem.setParent(findItem);
            findItem.getNextLevel().add(addMenuItem);
            return true;
        }
        return false;
    }

    public boolean addByParent(MenuItem parent,MenuItem... addMenuItems){
        return addByParentId(parent.getId(),addMenuItems);
    }

    public boolean addByParentId(Long id, MenuItem... addMenuItems){
        if(id == null) return false;
        MenuItem findItem = rootItem.getMenuItemById(id);
        if(findItem != null){
            if(findItem.getNextLevel() == null){
                findItem.setNextLevel(new ArrayList<MenuItem>());
            }
            Collections.addAll(findItem.getNextLevel(), addMenuItems);
            return true;
        }
        return false;
    }

    public MenuItem getRootItem(){
        return rootItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.rootItem);
    }

    protected MenuItemTree(Parcel in) {
        this.rootItem = (MenuItem) in.readSerializable();
    }

    public static final Creator<MenuItemTree> CREATOR = new Creator<MenuItemTree>() {
        @Override
        public MenuItemTree createFromParcel(Parcel source) {
            return new MenuItemTree(source);
        }

        @Override
        public MenuItemTree[] newArray(int size) {
            return new MenuItemTree[size];
        }
    };
}
