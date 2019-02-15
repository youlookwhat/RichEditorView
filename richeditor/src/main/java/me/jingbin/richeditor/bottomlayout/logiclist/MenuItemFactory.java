package me.jingbin.richeditor.bottomlayout.logiclist;

import android.content.Context;
import android.view.View;

import me.jingbin.richeditor.bottomlayout.menuitem.ImageViewButtonItem;
import me.jingbin.richeditor.bottomlayout.menuitem.TextViewItem;


/**
 * Created by jingbin on 2018/11/23.
 */

public class MenuItemFactory {

    public static MenuItem generateMenuItem(long id, View contentView){
        return new MenuItem(id,contentView);
    }

    public static ImageViewButtonItem generateImageItem(Context context, long id, int uri, boolean b){
        return new ImageViewButtonItem(context,generateMenuItem(id,null),uri,b);
    }

    public static ImageViewButtonItem generateImageItem(Context context, long id, int uri){
        return new ImageViewButtonItem(context,generateMenuItem(id,null),uri);
    }

    public static ImageViewButtonItem generateImageItem(Context context, int uri, boolean b){
        return new ImageViewButtonItem(context,generateMenuItem(0x00,null),uri,b);
    }

    public static TextViewItem generateTextItem(Context context, long id, String text){
        return new TextViewItem(context,generateMenuItem(id,null), text);
    }

//    public static ToggleButtonItem generateToggleButtonItem(Context context, long id, int idres){
//        return new ToggleButtonItem(context,generateMenuItem(id,null), idres);
//    }

}
