package me.jingbin.richeditor.editrichview.factory;

import android.content.Context;

import me.jingbin.richeditor.bottomlayout.api.IBottomMenuItem;
import me.jingbin.richeditor.bottomlayout.menuitem.AbstractBottomMenuItem;


/**
 * Created by jingbin on 2018/11/29.
 */

interface IItemFactory<T extends AbstractBottomMenuItem> {
    T generateItem(Context context, Long id, IBottomMenuItem.OnBottomItemClickListener listener);
}
