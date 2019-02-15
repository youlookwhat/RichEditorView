package me.jingbin.richeditor.editrichview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import me.jingbin.richeditor.bottomlayout.LuBottomMenu;
import me.jingbin.richeditor.bottomlayout.api.IBottomMenuItem;
import me.jingbin.richeditor.bottomlayout.logiclist.MenuItem;
import me.jingbin.richeditor.bottomlayout.menuitem.AbstractBottomMenuItem;
import me.jingbin.richeditor.editrichview.base.RichEditor;
import me.jingbin.richeditor.editrichview.factory.BaseItemFactory;
import me.jingbin.richeditor.editrichview.factory.DefaultItemFactory;


/**
 * Created by jingbin on 2018/11/15.
 */

public class SimpleRichEditor extends RichEditor {

    private LuBottomMenu mLuBottomMenu;
    private SelectController mSelectController;
    //不受其他items点击事件影响的items
    private ItemIndex.Register mRegister;
    private ArrayList<Long> mFreeItems;
    private OnStateChangeListener mOnStateChangeListener;
    private BaseItemFactory mBaseItemFactory;
    private OnButtonClickListener mOnButtonClickListener;

    public SimpleRichEditor(Context context) {
        super(context);
    }

    public SimpleRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public void setOnStateChangeListener(OnStateChangeListener mOnStateChangeListener) {
        this.mOnStateChangeListener = mOnStateChangeListener;
    }

    public BaseItemFactory getBaseItemFactory() {
        if (mBaseItemFactory == null) {
            mBaseItemFactory = createDefaultFactory();
        }
        return mBaseItemFactory;
    }

    private DefaultItemFactory createDefaultFactory() {
        return new DefaultItemFactory();
    }


    /**
     * @param baseItemFactory the bottomItem factory that will override the default factory
     *                        设置新的工厂方法生产自定义的底栏 Item 项
     */
    public void setBaseItemFactory(BaseItemFactory baseItemFactory) {
        this.mBaseItemFactory = baseItemFactory;
    }

    public void setLuBottomMenu(@NonNull LuBottomMenu mLuBottomMenu) {
        this.mLuBottomMenu = mLuBottomMenu;
        init();
        initRichTextViewListeners();
    }

    private void init() {
        mSelectController = SelectController.createController();
        mRegister = ItemIndex.getInstance().getRegister();
        mFreeItems = new ArrayList<>();

        addImage();
        addProduct();
        addTypefaceBranch(true, true, true, true, true);
        addLine();
        addUndo();
        addRedo();

        mSelectController.setHandler(new SelectController.StatesTransHandler() {
            @Override
            public void handleA2B(long id) {
                if (id > 0) {
                    mLuBottomMenu.setItemSelected(id, true);
                }
            }

            @Override
            public void handleB2A(long id) {
                if (id > 0) {
                    mLuBottomMenu.setItemSelected(id, false);
                }
            }
        });
    }

    private void initRichTextViewListeners() {
        setOnDecorationChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChangeListener(String text, List<Type> types) {
                onStateChange(text, types);

                for (long id : mFreeItems) {
                    mLuBottomMenu.setItemSelected(id, false);
                }
                mSelectController.reset();
                for (RichEditor.Type t : types) {
                    if (!mSelectController.contain(t.getTypeCode())) {
                        mLuBottomMenu.setItemSelected(t.getTypeCode(), true);
                    } else {
                        mSelectController.changeState(t.getTypeCode());
                    }
                }

            }
        });
        /**焦点获取监听*/
        setOnFocusChangeListener(new RichEditor.OnFocusChangeListener() {
            @Override
            public void onFocusChange(boolean isFocus) {
                if (!isFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mLuBottomMenu.show(200);
                    }
                } else {
                    mLuBottomMenu.hide(200);
                }

            }
        });

        setOnInitialLoadListener(new RichEditor.AfterInitialLoadListener() {
            @Override
            public void onAfterInitialLoad(boolean isReady) {
                if (isReady) {
                    focusEditor();
                }
            }
        });
    }

    public SimpleRichEditor addUndo() {
        checkNull(mLuBottomMenu);

        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(
                getContext(),
                ItemIndex.UNDO,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        undo();
                        return false;
                    }
                }));
        return this;
    }

    public SimpleRichEditor addRedo() {
        checkNull(mLuBottomMenu);

        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(),
                ItemIndex.REDO,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        redo();
                        return false;
                    }
                }));
        return this;
    }

    public SimpleRichEditor addImage() {
        checkNull(mLuBottomMenu);
        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(),
                ItemIndex.INSERT_IMAGE,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        if (mOnButtonClickListener != null) {
                            mOnButtonClickListener.addImage();
                        }
                        return false;
                    }
                }));
        return this;
    }

    public SimpleRichEditor addProduct() {
        checkNull(mLuBottomMenu);
        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(),
                ItemIndex.INSERT_PRODUCT,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        if (mOnButtonClickListener != null) {
                            mOnButtonClickListener.addProduct();
                        }
                        return false;
                    }
                }));
        return this;
    }

    public SimpleRichEditor addLine() {
        checkNull(mLuBottomMenu);
        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(),
                ItemIndex.INSERT_LINE,
                new IBottomMenuItem.OnBottomItemClickListener() {
                    @Override
                    public boolean onItemClick(MenuItem item, boolean isSelected) {
                        // 分割线
                        insertHr();
                        return false;
                    }
                }));
        return this;
    }


    /**
     * @param text  传入的字段
     * @param types 含有的类型
     *              自定义时添加监听以实现自定义按钮的逻辑
     */
    private void onStateChange(String text, List<Type> types) {
        if (mOnStateChangeListener != null) {
            mOnStateChangeListener.onStateChangeListener(text, types);
        }
    }


    private boolean isInSelectController(long id) {
        if (mSelectController.contain(id)) {
            mSelectController.changeState(id);
            return true;
        }
        return false;
    }


    public SimpleRichEditor addTypefaceBranch(boolean needBold, boolean needItalic, boolean needStrikeThrough, boolean needBlockQuote, boolean needH) {
        checkNull(mLuBottomMenu);

        if (!(needBlockQuote || needBold || needH || needItalic || needStrikeThrough)) {
            return this;
        }
        if (needBlockQuote) {
            mSelectController.add(ItemIndex.BLOCK_QUOTE);
        }
        if (needH) {
            mSelectController.addAll(ItemIndex.H1, ItemIndex.H2, ItemIndex.H3, ItemIndex.H4);
        }

        if (needBold) {
            mFreeItems.add(ItemIndex.BOLD);
        }
        if (needItalic) {
            mFreeItems.add(ItemIndex.ITALIC);
        }
        if (needStrikeThrough) {
            mFreeItems.add(ItemIndex.STRIKE_THROUGH);
        }

        mLuBottomMenu.addRootItem(getBaseItemFactory().generateItem(getContext(), ItemIndex.A))
                .addItem(ItemIndex.A, needBold ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.BOLD,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setBold();
                                Log.e("onItemClick", item.getId() + "");

                                //不拦截不在选择控制器中的元素让Menu自己控制选择显示效果
                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needItalic ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.ITALIC,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setItalic();
                                Log.e("onItemClick", item.getId() + "");

                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needStrikeThrough ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.STRIKE_THROUGH,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setStrikeThrough();
                                Log.e("onItemClick", item.getId() + "");

                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needBlockQuote ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.BLOCK_QUOTE,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setBlockquote(!isSelected);
                                Log.e("onItemClick", item.getId() + "");

                                //mSelectController.changeState(ItemIndex.BLOCK_QUOTE);
                                return isInSelectController(item.getId());
                            }
                        }) : null)

                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H1,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(1, !isSelected);
                                Log.e("onItemClick", item.getId() + "");

                                //mSelectController.changeState(ItemIndex.H1);
                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H2,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(2, !isSelected);
                                //mSelectController.changeState(ItemIndex.H2);
                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H3,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(3, !isSelected);
                                //mSelectController.changeState(ItemIndex.H3);
                                return isInSelectController(item.getId());
                            }
                        }) : null)
                .addItem(ItemIndex.A, needH ? getBaseItemFactory().generateItem(
                        getContext(),
                        ItemIndex.H4,
                        new IBottomMenuItem.OnBottomItemClickListener() {
                            @Override
                            public boolean onItemClick(MenuItem item, boolean isSelected) {
                                setHeading(4, !isSelected);
                                //mSelectController.changeState(ItemIndex.H4);
                                return isInSelectController(item.getId());
                            }
                        }) : null);
        return this;
    }


    @SuppressWarnings("unused")
    public SimpleRichEditor addCustomItem(long parentId, long id, AbstractBottomMenuItem item) {
        checkNull(mLuBottomMenu);

        if (!mRegister.hasRegister(parentId)) {
            throw new RuntimeException(parentId + ":" + ItemIndex.NO_REGISTER_EXCEPTION);
        }
        if (mRegister.isDefaultId(id)) {
            throw new RuntimeException(id + ":" + ItemIndex.HAS_REGISTER_EXCEPTION);
        }

        if (!mRegister.hasRegister(id)) {
            mRegister.register(id);
        }

        item.getMenuItem().setId(id);
        mLuBottomMenu.addItem(parentId, item);
        return this;
    }

    private void checkNull(Object obj) {
        if (obj == null) {
            throw new RuntimeException("object can't be null");
        }
    }

    public interface OnButtonClickListener {

        void addImage();

        void addProduct();
    }

    public void setOnButtonClickListener(OnButtonClickListener mOnButtonClickListener) {
        this.mOnButtonClickListener = mOnButtonClickListener;
    }
}
