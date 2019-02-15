package me.jingbin.richeditorview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import me.jingbin.richeditor.bottomlayout.LuBottomMenu;
import me.jingbin.richeditor.editrichview.SimpleRichEditor;
import me.jingbin.richeditor.editrichview.base.RichEditor;
import me.jingbin.richeditorview.tools.GoodsBean;
import me.jingbin.richeditorview.tools.KeyBoardListener;
import me.jingbin.richeditorview.tools.Tools;

public class MainActivity extends AppCompatActivity {

    private final String headerImageSrc = "https://upload.jianshu.io/admin_banners/web_images/4611/5645ed8603a55d79042f2f7d8e7cc1d533cc30ac.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/1250/h/540";
    private final String contentImageSrc = "https://upload-images.jianshu.io/upload_images/15152899-e1a43b1cca2a4d58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp";
    private SimpleRichEditor richEditor;
    private LuBottomMenu luBottomMenu;
    private Toolbar mToolbar;
    private String mTitle = "";
    private String mContent = "";
    private long mContentLength = 0;
    private boolean isShowDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        richEditor = findViewById(R.id.rich_edit);
        luBottomMenu = findViewById(R.id.lu_bottom_menu);

        initView();
        KeyBoardListener.getInstance(this).init();
    }


    private void initView() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        //去除默认Title显示
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.mipmap.actionbar_more));
        mToolbar.setTitle(0 + "字");
        richEditor.setLuBottomMenu(luBottomMenu);
        richEditor.setOnTextLengthChangeListener(new RichEditor.OnTextLengthChangeListener() {
            @Override
            public void onTextLengthChange(final long length) {
                mToolbar.post(new Runnable() {
                    @Override
                    public void run() {
                        mToolbar.setTitle(length + "字");
                        mContentLength = length;
                    }
                });
            }
        });
        richEditor.setOnOutHandleListener(new RichEditor.OnOutHandleListener() {
            @Override
            public void onClickHeaderImageListener() {
                // 封面图 这在子线程
                richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 主线程
                        Toast.makeText(MainActivity.this, "点击了封面", Toast.LENGTH_SHORT).show();
                        richEditor.edUpcover(headerImageSrc);
                    }
                }, 70);
            }

            @Override
            public void onGetTitleContent(final String title, final String content) {
                Log.e("RichEdit", "---获取标题：" + title);
                Log.e("RichEdit", "---获取内容：" + content);
                richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isShowDialog) {
                            Tools.show(richEditor, title, content, "保存", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTitle = title;
                                    mContent = content;
                                    Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            mTitle = title;
                            mContent = content;
                            Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 10);
            }
        });
        richEditor.setOnButtonClickListener(new SimpleRichEditor.OnButtonClickListener() {
            @Override
            public void addImage() {
                // 添加图片
                Tools.hideSoftKeyBoard(MainActivity.this);
                richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        richEditor.edAddimgsrc(contentImageSrc);
                    }
                }, 70);
            }

            @Override
            public void addProduct() {
                // 添加产品
                Tools.hideSoftKeyBoard(MainActivity.this);
                GoodsBean goodsBean = new GoodsBean();
                goodsBean.setTitle("title");
                goodsBean.setAlias("alias");
                goodsBean.setImageSrc("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3509424840,3355088205&fm=179&app=42&f=JPEG?w=56&h=56");
                // 添加产品(主线程)
                richEditor.edAddProduct(123, new Gson().toJson(goodsBean));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_get:
                isShowDialog = true;
                richEditor.edThishtml();
                break;
            case R.id.actionbar_clear:
                richEditor.edOutdata("", "");
                mToolbar.setTitle("0字");
                break;
            case R.id.actionbar_save:
                isShowDialog = false;
                richEditor.edThishtml();
                break;
            case R.id.actionbar_show:
                if (!TextUtils.isEmpty(mTitle) || TextUtils.isEmpty(mContent)) {
                    // 回显 标题和内容
                    richEditor.edOutdata(mTitle, mContent);
                    if (!TextUtils.isEmpty(mContent)) {
                        mToolbar.setTitle(mContentLength + "字");
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
