package me.jingbin.richeditor.editrichview.factory;

import android.content.Context;
import android.support.annotation.DrawableRes;

import me.jingbin.richeditor.R;
import me.jingbin.richeditor.bottomlayout.api.IBottomMenuItem;
import me.jingbin.richeditor.bottomlayout.logiclist.MenuItemFactory;
import me.jingbin.richeditor.bottomlayout.menuitem.ImageViewButtonItem;
import me.jingbin.richeditor.editrichview.ItemIndex;


/**
 * public static final long INSERT_IMAGE = 0x01;
 * public static final long A = 0x02;
 * public static final long MORE = 0x03;
 * public static final long UNDO = 0x04;
 * public static final long REDO = 0x05;
 * public static final long BOLD = 0x06 ;
 * public static final long ITALIC = 0x07 ;
 * public static final long STRIKE_THROUGH = 0x08 ;
 * public static final long BLOCK_QUOTE = 0x09 ;
 * public static final long H1 = 0x0a ;
 * public static final long H2 = 0x0b ;
 * public static final long H3 = 0x0c ;
 * public static final long H4 = 0x0d;
 * public static final long HALVING_LINE = 0x0e;
 * public static final long LINK = 0x0f;
 *
 * for custom the default factory ,to replace the DrawRes ,you can override the "protected" function
 * or try to make a non-ImageViewButton item by write the subclass of the BaseItemFactory and set the factory in
 * the richtexteditor
 * Created by jingbin on 2018/11/23.
 */

public class DefaultItemFactory extends BaseItemFactory<ImageViewButtonItem>{

    private ImageViewButtonItem generateItem(Context context, long itemIndex, @DrawableRes int id) {
        return MenuItemFactory.generateImageItem(context, itemIndex, id, false);
    }

    private ImageViewButtonItem generateAutoSetItem(Context context, long itemIndex, @DrawableRes int id) {
        return MenuItemFactory.generateImageItem(context, itemIndex, id, true);
    }

    protected  ImageViewButtonItem generateInsertImageItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.INSERT_IMAGE, R.drawable.icon_image);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateInsertProductItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.INSERT_PRODUCT, R.drawable.icon_product);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateInsertLineItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.INSERT_LINE, R.drawable.icon_line);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateAItem(Context context) {
        return generateAutoSetItem(context, ItemIndex.A, R.drawable.icon_a);
    }

    protected  ImageViewButtonItem generateMoreItem(Context context) {
        return generateAutoSetItem(context, ItemIndex.MORE, R.drawable.more);
    }

    protected  ImageViewButtonItem generateUndoItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.UNDO, R.drawable.undo);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateRedoItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.REDO, R.drawable.redo);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateBoldItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.BOLD, R.drawable.icon_bold);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateItalicItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.ITALIC, R.drawable.icon_italic);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateStrikeThroughItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.STRIKE_THROUGH, R.drawable.strikethrough);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateBlockQuoteItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.BLOCK_QUOTE, R.drawable.icon_blockquote);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateH1Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H1, R.drawable.icon_h1);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateH2Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H2, R.drawable.icon_h2);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateH3Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H3, R.drawable.icon_h3);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected  ImageViewButtonItem generateH4Item(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateAutoSetItem(context, ItemIndex.H4, R.drawable.icon_h4);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected   ImageViewButtonItem generateHalvingLineItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.HALVING_LINE, R.drawable.icon_line);
        item.setOnItemClickListener(listener);
        return item;
    }

    protected   ImageViewButtonItem generateLinkItem(Context context, IBottomMenuItem.OnBottomItemClickListener listener) {
        ImageViewButtonItem item = generateItem(context, ItemIndex.LINK, R.drawable.link);
        item.setOnItemClickListener(listener);
        return item;
    }

    @Override
    public  ImageViewButtonItem generateItem(Context context, Long id, IBottomMenuItem.OnBottomItemClickListener listener) {
        switch (id.intValue()){
            case (int) ItemIndex.BOLD:
                return generateBoldItem(context, listener);
            case (int) ItemIndex.ITALIC:
                return generateItalicItem(context, listener);
            case (int) ItemIndex.STRIKE_THROUGH:
                return generateStrikeThroughItem(context, listener);
            case (int) ItemIndex.BLOCK_QUOTE:
                return generateBlockQuoteItem(context, listener);
            case (int) ItemIndex.H1:
                return generateH1Item(context, listener);
            case (int) ItemIndex.H2:
                return generateH2Item(context, listener);
            case (int) ItemIndex.H3:
                return generateH3Item(context, listener);
            case (int) ItemIndex.H4:
                return generateH4Item(context, listener);
            case (int) ItemIndex.HALVING_LINE:
                return generateHalvingLineItem(context,listener);
            case (int) ItemIndex.LINK:
                return generateLinkItem(context, listener);
            case (int) ItemIndex.REDO:
                return generateRedoItem(context,listener);
            case (int) ItemIndex.UNDO:
                return generateUndoItem(context,listener);
            case (int) ItemIndex.INSERT_IMAGE:
                return generateInsertImageItem(context,listener);
            case (int) ItemIndex.INSERT_PRODUCT:
                return generateInsertProductItem(context,listener);
            case (int) ItemIndex.INSERT_LINE:
                return generateInsertLineItem(context,listener);
            default:
                return null;
        }
    }

    @Override
    public ImageViewButtonItem generateItem(Context context, Long id) {
        switch (id.intValue()){
            case (int) ItemIndex.A:
                return generateAItem(context);
            case (int) ItemIndex.MORE:
                return generateMoreItem(context);
            default:
                return null;
        }
    }
}
