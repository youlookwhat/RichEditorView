package me.jingbin.richeditor.bottomlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.jingbin.richeditor.bottomlayout.api.ITheme;
import me.jingbin.richeditor.bottomlayout.logiclist.MenuItem;
import me.jingbin.richeditor.bottomlayout.logiclist.MenuItemTree;
import me.jingbin.richeditor.bottomlayout.menuitem.AbstractBottomMenuItem;
import me.jingbin.richeditor.bottomlayout.theme.LightTheme;


/**
 * 多叉分支的底部菜单
 * Created by jingbin on 2018/11/20.
 */
@SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
public class LuBottomMenu extends ViewGroup {

    private final static int DEFAULT_HEIGHT = 54;//dp

    // 大于可滑动
    private static int MAX_NUM_ONE_ROW = 9;
    private static int MAX_LEVELS = 3;
    private static int INNER_LAYOUT_PADDING_L = 0;
    private static int INNER_LAYOUT_PADDING_R = 0;
    private static int INNER_LAYOUT_PADDING_T = 0;
    private static int INNER_LAYOUT_PADDING_B = 0;
//    private static float INNER_ITEM_PADDING_RATE = 0.44f;//内部图标内边距充填0.56
    private static float INNER_ITEM_PADDING_RATE = 0f;

    private int[] colorSet;

    private MenuItemTree mMenuTree;
    private HashMap<Long, AbstractBottomMenuItem> mBottomMenuItems;
    private ArrayDeque<MenuItem> mPathRecord;//菜单展开路径栈,通过存储这个信息配合逻辑树恢复其他信息，时间换空间
    private ArrayList<LinearLayout> mDisplayMenus;

    private MenuItem mCurMenuItem;//当前聚焦项目集合的最顶层父请节点
    private int mDisplayRowNum = 0;//显示的菜单行数
    private int mSingleRowHeight = 0;//每级菜单的高度

    private Paint mPaint;
    private ITheme mTheme;

    private boolean isFirstMeasure = true;
    private boolean isFirstLayout = true;
    // 需要线？
    private boolean needLine = false;

    private AbstractBottomMenuItem.OnItemClickListener mOnItemClickListener;

    private AbstractBottomMenuItem.OnItemClickListener mInnerListener;

    public LuBottomMenu(Context context) {
        this(context, null);
    }

    public LuBottomMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuBottomMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (needLine) {
            mPaint = new Paint();
            mPaint.setAlpha(127);
        }
    }

    /**
     * @param widthMeasureSpec  宽度测量信息
     * @param heightMeasureSpec 高度测量信息
     *                          <p>
     *                          为了适配wrap_content 会进行2次测量，第一次检测mode 第二次正真排布
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        int width = Math.max(widthSize, 0);

        //注意这里没有break
        switch (heightMode) {
            //若允许任意大小，则设置最大层数为5层并继续接下来的设置
            case MeasureSpec.UNSPECIFIED:
                MAX_LEVELS = 5;
                //若为WRAP_CONTENT时设置默认高度54dp
            case MeasureSpec.AT_MOST:
                heightSize = Utils.dip2px(getContext(), DEFAULT_HEIGHT);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                //大部分情况下为精确计算宽高
            case MeasureSpec.EXACTLY:
                mSingleRowHeight = Math.max(heightSize - getPaddingBottom() - getPaddingTop(), 0);
                break;
        }

        final int height;
        //逻辑树中至少除了root节点外含有一行
        if (mDisplayRowNum < 1) {
            height = mSingleRowHeight + getPaddingTop() + getPaddingBottom();
        } else {
            height = mDisplayRowNum * mSingleRowHeight + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);

        //如果是第一次测测量移除无用的view ，不对子view进行测量
        if (isFirstMeasure) {
            removeUselessViews();
            isFirstMeasure = false;
            AnimatorUtil.setTransition(this, width, height);
            return;
        }

        final int childCount = getChildCount();

        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //第一次layout根据第一次measure的高度进行添加不进行子view的layout
        if (isFirstLayout) {
            isFirstLayout = false;
            if (mPathRecord.isEmpty()) {
                //初始化底栏的根目录级
                addOneLevel();
            }
            return;
        }

        final int childCount = getChildCount();
        final int topBase = getPaddingTop();
        final int leftBase = getPaddingLeft();
        final int width = r - l;
        final int height = b - t;

        int offset;
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                offset = mDisplayRowNum - 1 - i;
                child.layout(leftBase + child.getPaddingLeft(), topBase + offset * mSingleRowHeight + child.getPaddingTop(),
                        width - getPaddingRight() - child.getPaddingRight(), topBase + (offset + 1) * mSingleRowHeight - child.getPaddingBottom());
            }
        }

    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean success = super.drawChild(canvas, child, drawingTime);
        if (needLine) {
            drawLine(canvas);
        }
        return success;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state != null) {
            SaveState ss = (SaveState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            mTheme = ss.theme;
            colorSet = mTheme.getBackGroundColors();
            restoreAllInfo(ss.pathRecord);//根据路径信息恢复其他信息
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SaveState ss = new SaveState(superState);
        ss.pathRecord = mPathRecord;
        ss.theme = mTheme;
        return ss;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Set<Map.Entry<Long, AbstractBottomMenuItem>> entrySet = mBottomMenuItems.entrySet();
        for (Map.Entry<Long, AbstractBottomMenuItem> e :
                entrySet) {
            e.getValue().onViewDestroy();
        }
        removeAllLevels(mDisplayRowNum);
    }

    /**
     * 在 init()之后调用
     */
    private void removeUselessViews() {
        final int childCount = getChildCount();
        if (childCount > mDisplayMenus.size()) {
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                //noinspection SuspiciousMethodCalls
                if (!mDisplayMenus.contains(v)) {
                    removeView(v);
                }
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    private void init() {
        mMenuTree = new MenuItemTree();
        mDisplayMenus = new ArrayList<>();
        mPathRecord = new ArrayDeque<>();
        mBottomMenuItems = new HashMap<>();

        mInnerListener = new AbstractBottomMenuItem.OnItemClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem) {
                onItemClickPreHandle(menuItem);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(menuItem);
                }
            }
        };

        if (mCurMenuItem == null || mCurMenuItem.isLeafNode()) {
            mDisplayRowNum = 0;
        }

        mCurMenuItem = mMenuTree.getRootItem();
    }


    /**
     * @param menuItem bottomitem 点击后的预处理事件
     */
    private void onItemClickPreHandle(MenuItem menuItem) {
        if (!menuItem.equals(mCurMenuItem)) {
            if (!menuItem.isLeafNode()) {
                boolean isOldItemChild = menuItem.getDeep() > mCurMenuItem.getDeep();
                boolean isOldItemElder = (!isOldItemChild && menuItem.isElderOf(mCurMenuItem));//直系父亲节点
                mCurMenuItem = menuItem;

                if (isOldItemChild) {
                    addOneLevel();
                } else {
                    removeAllLevels(Math.max(0, mDisplayRowNum - mCurMenuItem.getDeep()));
                    if (!isOldItemElder) {
                        addOneLevel();
                    } else {
                        mCurMenuItem = mCurMenuItem.getParent();
                    }
                }
            } else {
                AbstractBottomMenuItem newItem = getBottomMenuItem(menuItem);
                newItem.setSelected(!newItem.isSelected());
            }
        } else {
            if (!menuItem.isLeafNode()) {
                removeAllLevels(mDisplayRowNum - mCurMenuItem.getDeep());
                mCurMenuItem = mCurMenuItem.getParent();
            } else {
                AbstractBottomMenuItem newItem = getBottomMenuItem(menuItem);
                newItem.setSelected(!newItem.isSelected());
            }
        }
    }

    /**
     * 增加一行菜单当未超过最大限制并且不是逻辑树中的叶子节点时
     */
    private void addOneLevel() {
        if (mCurMenuItem == null || mCurMenuItem.isLeafNode() || mCurMenuItem.getDeep() >= MAX_LEVELS - 1) {
            return;
        }
        LinearLayout linearLayout = new LinearLayout(getContext());
        if (mTheme == null) {
            createDefaultTheme();
        }

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(getColorByDeep(mCurMenuItem.getDeep()));
        linearLayout.setPadding(INNER_LAYOUT_PADDING_L, INNER_LAYOUT_PADDING_T,
                INNER_LAYOUT_PADDING_R, INNER_LAYOUT_PADDING_B);
        linearLayout.setLayoutParams(new ViewGroup.MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mDisplayMenus.add(linearLayout);
        fillMenu(linearLayout, mCurMenuItem);
        addView(linearLayout);
        mPathRecord.push(mCurMenuItem);
        if (mCurMenuItem != null && !mCurMenuItem.equals(mMenuTree.getRootItem())) {
            getBottomMenuItem(mCurMenuItem).setSelected(true);
        }
        mDisplayRowNum++;
    }


    /**
     * unused
     */
    private void removeTopLevel() {
        removeAllLevels(1);
    }

    private void removeAllLevels(int num) {
        if (num >= mDisplayRowNum || num < 1) {
            return;
        }

        int b = mDisplayRowNum - num;
        for (int i = mDisplayRowNum - 1; i >= b; i--) {
            if (mDisplayMenus.get(i).getChildAt(0) instanceof HorizontalScrollView) {
                View v = ((HorizontalScrollView) mDisplayMenus.get(i).getChildAt(0)).getChildAt(0);
                if (v != null && v instanceof LinearLayout) {
                    ((LinearLayout) v).removeAllViews();
                }
            }
            mDisplayMenus.get(i).removeAllViews();
            mDisplayMenus.remove(i);
            removeView(getChildAt(i));
            getBottomMenuItem(mPathRecord.peek()).setSelected(false);
            mPathRecord.pop();
            mDisplayRowNum--;
        }
    }


    /**
     * @param parent 聚焦节点
     *               填充顶部的菜单行
     */
    private void fillTopMenu(MenuItem parent) {
        if (mDisplayRowNum >= 1) {
            fillMenu(mDisplayMenus.get(mDisplayRowNum - 1), parent);
        }
    }

    /**
     * @param menu   需要填充的布局
     * @param parent 聚焦节点
     *               填充对应布局根据聚焦的节点的子节点
     */
    private void fillMenu(LinearLayout menu, MenuItem parent) {

        if (parent.getNextLevel() == null) {
            return;
        }
        final int count = parent.getNextLevel().size();
        View v;

        if (count > MAX_NUM_ONE_ROW) {
            HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
            scrollView.setSmoothScrollingEnabled(true);
            scrollView.setHorizontalScrollBarEnabled(false);
            menu.addView(scrollView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            LinearLayout linearLayout = new LinearLayout(getContext());
            for (MenuItem item :
                    parent.getNextLevel()) {
                AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
                bottomMenuItem.setOnItemClickListener(mInnerListener);
                bottomMenuItem.setTheme(mTheme);
                //当恢复时Context对象无法序列化，这里临时传入
                if (bottomMenuItem.getContext() == null) {
                    bottomMenuItem.setContext(getContext());
                }
                bottomMenuItem.onDisplayPrepare();

                v = bottomMenuItem.getMainView();

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                v.setLayoutParams(lp);
                if (mSingleRowHeight == 0) {
                    mSingleRowHeight = getLayoutParams().height;
                }
                int padding = (int) (mSingleRowHeight * (1 - INNER_ITEM_PADDING_RATE) / 2);
                v.setPadding(padding / 3, padding, padding / 3, padding);
                linearLayout.addView(v);
            }
            scrollView.addView(linearLayout, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        } else {
            for (MenuItem item : parent.getNextLevel()) {
                AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
                bottomMenuItem.setOnItemClickListener(mInnerListener);
                //当恢复时Context对象无法序列化，这里临时传入
                bottomMenuItem.setTheme(mTheme);
                if (bottomMenuItem.getContext() == null) {
                    bottomMenuItem.setContext(getContext());
                }
                bottomMenuItem.onDisplayPrepare();

                v = bottomMenuItem.getMainView();
                if (mSingleRowHeight == 0) {
                    mSingleRowHeight = getLayoutParams().height;
                }
                int padding = (int) (mSingleRowHeight * (1 - INNER_ITEM_PADDING_RATE) / 2);
//                v.setPadding(padding / 3, padding, padding / 3, padding);
                v.setPadding(0, 9, 0, 9);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                lp.weight = 1;
                v.setLayoutParams(lp);
                menu.addView(v);
            }
        }
    }

    private AbstractBottomMenuItem getBottomMenuItem(MenuItem item) {
        return mBottomMenuItems.get(item.getId());
    }

    /**
     * @param pathRecord 点击的展开路径记录
     *                   通过路径直接恢复视图序列
     */
    private void restoreAllInfo(ArrayDeque<MenuItem> pathRecord) {
        mPathRecord.clear();
        while (!pathRecord.isEmpty()) {
            mCurMenuItem = pathRecord.getLast();
            addOneLevel();
            pathRecord.removeLast();
        }

    }


    /**
     * @param deep 当前聚焦项的深度
     * @return 在颜色列表中对应的颜色
     */
    private int getColorByDeep(int deep) {
        return colorSet[deep % colorSet.length];
    }

    /**
     * @param canvas 根据背景色绘制Menu顶部的分割线
     */
    private void drawLine(Canvas canvas) {
        if (getLayoutTransition() != null && getLayoutTransition().isRunning()) {
            return;
        }
        mPaint.setColor(Utils.getDarkerColor(getColorByDeep(mDisplayRowNum), 0.2f));
        mPaint.setAlpha(80);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            canvas.drawLine(getPaddingStart(), getPaddingTop(), getWidth() - getPaddingEnd(), getPaddingTop(), mPaint);
        } else {
            canvas.drawLine(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop(), mPaint);
        }
    }

    private ITheme createDefaultTheme() {
        mTheme = new LightTheme();
        colorSet = mTheme.getBackGroundColors();
        return mTheme;
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public static class SaveState extends BaseSavedState {
        ArrayDeque<MenuItem> pathRecord;
        ITheme theme;

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeSerializable(this.pathRecord);
            dest.writeParcelable(this.theme, flags);
        }

        protected SaveState(Parcel in) {
            super(in);
            this.pathRecord = (ArrayDeque<MenuItem>) in.readSerializable();
            this.theme = in.readParcelable(ITheme.class.getClassLoader());
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel source) {
                return new SaveState(source);
            }

            @Override
            public SaveState[] newArray(int size) {
                return new SaveState[size];
            }
        };
    }

    //=========================================== 外部调用 =========================================================================================

    public void setInnerLayoutPadding(int l, int t, int r, int b) {
        INNER_LAYOUT_PADDING_L = l;
        INNER_LAYOUT_PADDING_R = r;
        INNER_LAYOUT_PADDING_T = t;
        INNER_LAYOUT_PADDING_B = b;
        invalidate();
    }

    public void setOnItemClickListener(AbstractBottomMenuItem.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setItemSelected(long id, boolean isSelected) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null) {
            bottomMenuItem.setSelected(isSelected);
        }
    }

    //can't work now
    @Override
    public void setEnabled(boolean enabled) {
        Set<Map.Entry<Long, AbstractBottomMenuItem>> entrySet = mBottomMenuItems.entrySet();
        for (Map.Entry<Long, AbstractBottomMenuItem> e : entrySet) {
            if (e.getValue().getMainView() != null) {
                e.getValue().getMainView().setEnabled(enabled);
            }
        }
        super.setEnabled(enabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void show(long d) {
        AnimatorUtil.show(this, d);
    }

    public void hide(long d) {
        AnimatorUtil.hide(this, d);
    }

    public int isItemSelected2(MenuItem item) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
        if (bottomMenuItem != null) {
            return bottomMenuItem.isSelected() ? 1 : 0;
        } else {
            return -1;
        }
    }

    public int isItemSelected2(long id) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null) {
            return bottomMenuItem.isSelected() ? 1 : 0;
        } else {
            return -1;
        }
    }

    public boolean isItemSelected(MenuItem item) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(item);
        if (bottomMenuItem != null) {
            return bottomMenuItem.isSelected();
        } else {
            throw new RuntimeException("no item match");
        }
    }

    public boolean isItemSelected(long id) {
        AbstractBottomMenuItem bottomMenuItem = getBottomMenuItem(mMenuTree.getRootItem().getMenuItemById(id));
        if (bottomMenuItem != null) {
            return bottomMenuItem.isSelected();
        } else {
            throw new RuntimeException("no item match");
        }
    }

    public void setScrollModeNum(int num) {
        MAX_NUM_ONE_ROW = num;
    }

    public void setMenuBackGroundColor(int... colors) {
        colorSet = colors;
        if (!isFirstMeasure) {
            final int childrenCount = getChildCount();
            for (int i = 0; i < childrenCount; i++) {
                getChildAt(i).setBackgroundColor(getColorByDeep(i));
            }
            invalidate();
        }
    }

    /**
     * @param theme 主题色：custom theme
     */
    public void setTheme(ITheme theme) {
        mTheme = theme;
        colorSet = theme.getBackGroundColors();

        if (mDisplayMenus != null && !mDisplayMenus.isEmpty()) {
            Set<Map.Entry<Long, AbstractBottomMenuItem>> entries = mBottomMenuItems.entrySet();
            for (Map.Entry<Long, AbstractBottomMenuItem> e :
                    entries) {
                e.getValue().setThemeForDisplay(mTheme);
            }
        }

        int i = 0;
        if (mDisplayMenus != null) {
            setMenuBackGroundColor(colorSet);
        }
    }

    public LuBottomMenu addRootItem(AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) {
            return this;
        }
        MenuItem menuItem = bottomMenuItem.getMenuItem();
        mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);

        mMenuTree.addRootItem(menuItem);
        return this;
    }

    public LuBottomMenu addItem(MenuItem parentItem, AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) {
            return this;
        }
        MenuItem menuItem = bottomMenuItem.getMenuItem();
        if (parentItem == mMenuTree.getRootItem()) {
            addRootItem(bottomMenuItem);
        } else {
            mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            mMenuTree.addByParent(parentItem, menuItem);
        }
        return this;
    }

    public LuBottomMenu addItem(long parentId, AbstractBottomMenuItem bottomMenuItem) {
        if (bottomMenuItem == null) {
            return this;
        }
        MenuItem menuItem = bottomMenuItem.getMenuItem();

        mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
        mMenuTree.addByParentId(parentId, menuItem);
        return this;
    }

    public LuBottomMenu addItems(long parentId, AbstractBottomMenuItem... menuItems) {
        for (AbstractBottomMenuItem bottomMenuItem :
                menuItems) {
            addItem(parentId, bottomMenuItem);
        }
        return this;
    }

    public LuBottomMenu addItems(MenuItem parentItem, AbstractBottomMenuItem... menuItems) {
        MenuItem menuItem;
        for (AbstractBottomMenuItem bottomMenuItem :
                menuItems) {
            menuItem = bottomMenuItem.getMenuItem();

            mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            mMenuTree.addByParent(parentItem, menuItem);
        }
        return this;
    }

    public static class Builder {
        private LuBottomMenu luBottomMenu;

        public Builder(Context context) {
            luBottomMenu = new LuBottomMenu(context);
            if (luBottomMenu.mMenuTree == null) {
                luBottomMenu.mMenuTree = new MenuItemTree();
            }
            luBottomMenu.mCurMenuItem = luBottomMenu.mMenuTree.getRootItem();
            if (luBottomMenu.mCurMenuItem == null || luBottomMenu.mCurMenuItem.getNextLevel().isEmpty()) {
                luBottomMenu.mDisplayRowNum = 0;
            }
        }

        public Builder addRootItem(MenuItem menuItem) {
            luBottomMenu.mMenuTree.addRootItem(menuItem);
            if (luBottomMenu.mDisplayRowNum < 1) {
                luBottomMenu.mDisplayRowNum = 1;
            }
            return this;
        }

        public Builder addItem(MenuItem parentItem, AbstractBottomMenuItem bottomMenuItem) {
            MenuItem menuItem = bottomMenuItem.getMenuItem();
            luBottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            if (parentItem == luBottomMenu.mMenuTree.getRootItem()) {
                addRootItem(menuItem);
            } else {
                luBottomMenu.mMenuTree.addByParent(parentItem, menuItem);
            }
            return this;
        }

        public Builder addItem(long parentId, AbstractBottomMenuItem bottomMenuItem) {
            MenuItem menuItem = bottomMenuItem.getMenuItem();
            bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                @Override
                public void onItemClick(MenuItem menuItem) {
                    luBottomMenu.mCurMenuItem = menuItem;
                    if (luBottomMenu.mOnItemClickListener != null) {
                        luBottomMenu.mOnItemClickListener.onItemClick(menuItem);
                    }
                }
            });

            luBottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
            luBottomMenu.mMenuTree.addByParentId(parentId, menuItem);
            return this;
        }

        public Builder addItems(long parentId, AbstractBottomMenuItem... menuItems) {
            MenuItem menuItem;
            for (AbstractBottomMenuItem bottomMenuItem :
                    menuItems) {
                menuItem = bottomMenuItem.getMenuItem();
                bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem) {
                        luBottomMenu.mCurMenuItem = menuItem;
                        if (luBottomMenu.mOnItemClickListener != null) {
                            luBottomMenu.mOnItemClickListener.onItemClick(menuItem);
                        }
                    }
                });

                luBottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
                luBottomMenu.mMenuTree.addByParentId(parentId, menuItem);
            }
            return this;
        }

        public Builder addItems(MenuItem parentItem, AbstractBottomMenuItem... menuItems) {
            MenuItem menuItem;
            for (AbstractBottomMenuItem bottomMenuItem :
                    menuItems) {
                menuItem = bottomMenuItem.getMenuItem();
                bottomMenuItem.setOnItemClickListener(new AbstractBottomMenuItem.OnItemClickListener() {
                    @Override
                    public void onItemClick(MenuItem menuItem) {
                        luBottomMenu.mCurMenuItem = menuItem;
                        if (luBottomMenu.mOnItemClickListener != null) {
                            luBottomMenu.mOnItemClickListener.onItemClick(menuItem);
                        }
                    }
                });

                luBottomMenu.mBottomMenuItems.put(menuItem.getId(), bottomMenuItem);
                luBottomMenu.mMenuTree.addByParent(parentItem, menuItem);
            }
            return this;
        }

        public LuBottomMenu build() {
            return luBottomMenu;
        }
    }
}
