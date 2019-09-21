package me.jingbin.richeditor.editrichview.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * @author jingbin
 */
public abstract class RichEditor extends WebView {

    /**
     * 文字编辑的样式
     */
    public enum Type {
        /**
         * 加粗
         */
        BOLD(0x06),
        /**
         * 斜体
         */
        ITALIC(0x07),
        /**
         * 删除
         */
        STRIKETHROUGH(0x08),
        /**
         * 引用块
         */
        BLOCKQUOTE(0x09),
        H1(0x0a),
        H2(0x0b),
        H3(0x0c),
        H4(0x0d);

        //SUPERSCRIPT(1),//SUBSCRIPT(2),//UNDERLINE(3),
        private long typeCode;

        Type(long i) {
            typeCode = i;
        }

        public long getTypeCode() {
            return typeCode;
        }

        public boolean isMapTo(long id) {
            return typeCode == id;
        }
    }

    private static final String SETUP_HTML = "file:///android_asset/rich/editor.html";
    private static final String STATE_SCHEME = "state://";
    private static final String LINK_CHANGE_SCHEME = "change://";
    private static final String FOCUS_CHANGE_SCHEME = "focus://";
    private static final String IMAGE_CLICK_SCHEME = "image://";
    private boolean isReady = false;
    private String mContents = "";
    private long mContentLength;
    private OnStateChangeListener mStateChangeListener;
    private AfterInitialLoadListener mLoadListener;
    private OnScrollChangedCallback mOnScrollChangedCallback;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnImageClickListener mOnImageClickListener;
    private OnTextLengthChangeListener mOnTextLengthChangeListener;
    // 外部操作的接口
    private OnOutHandleListener mOnOutHandleListener;


    public RichEditor(Context context) {
        this(context, null);
    }

    public RichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint({"SetJavaScriptEnabled", "addJavascriptInterface"})
    public RichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        // 使用本地储存
        WebSettings settings = getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        // 进度条
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addJavascriptInterface(new Android4JsInterface(), "AndroidInterface");
        setWebViewClient(createWebViewClient());
        setWebChromeClient(new WebChromeClient());
        mContentLength = 0;
        load();
        //applyAttributes(context, attrs);
    }

    //--------------------------------------------------接口部分--------------------------------------------------

    /**
     * 供外部Activity操作的js接口
     * 包含传入的js和传出的js
     */
    public interface OnOutHandleListener {
        /**
         * 点击头部的封面图
         */
        void onClickHeaderImageListener();

        /**
         * 获取标题和内容的html
         */
        void onGetTitleContent(String title, String content);
    }


    public interface OnStateChangeListener {
        void onStateChangeListener(String text, List<Type> types);
    }

    public interface OnLinkClickListener {
        void onLinkClick(String linkName, String url);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(boolean isFocus);
    }

    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean isReady);
    }

    public interface OnImageClickListener {
        void onImageClick(Long url);
    }

    public interface OnTextLengthChangeListener {
        void onTextLengthChange(long length);
    }

    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();

    }


    public void setOnOutHandleListener(OnOutHandleListener onOutHandleListener) {
        this.mOnOutHandleListener = onOutHandleListener;
    }

    public void setOnTextLengthChangeListener(OnTextLengthChangeListener onTextLengthChangeListener) {
        this.mOnTextLengthChangeListener = onTextLengthChangeListener;
    }

    protected void setOnDecorationChangeListener(OnStateChangeListener listener) {
        mStateChangeListener = listener;
    }

    protected void setOnInitialLoadListener(AfterInitialLoadListener listener) {
        mLoadListener = listener;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    protected void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.mOnImageClickListener = onImageClickListener;
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    //--------------------------------------------------接口部分--------------------------------------------------

    private void imageClickCallBack(String url) {
        if (mOnImageClickListener != null) {
            mOnImageClickListener.onImageClick(Long.valueOf(url.replaceFirst(IMAGE_CLICK_SCHEME, "")));
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }
    }


    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    @SuppressWarnings("WeakerAccess")
    public interface OnScrollChangedCallback {
        void onScroll(int dx, int dy);
    }


    public void stateCheck(String text) {

        String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mStateChangeListener != null) {
            mStateChangeListener.onStateChangeListener(state, types);
        }
    }

    public void getHtmlAsyn() {
        exec("javascript:RE.getHtml4Android()");
    }

    public String getHtml() {
        return mContents;
    }

    public void load() {
        if (!isReady) {
            Log.e("load", "before load");
            loadUrl(SETUP_HTML);
            Log.e("load", "after load");
        }

    }

    protected void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    /**
     * 4.4以上可用 evaluateJavascript 效率高
     */
    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    protected class EditorWebVIewClient2 extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }

    private class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            Log.e("load", "after onPageFinished");

            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String decode;
            try {
                decode = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // No handling
                return false;
            }

            Log.e("jing", decode);

            if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                stateCheck(decode);
                return true;
            }

            if (TextUtils.indexOf(url, IMAGE_CLICK_SCHEME) == 0) {
                imageClickCallBack(decode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        // Android - WebView 加载 Https 出现 SSL Error. Failed to validate the certificate chain
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); //解决方案在此，不要调用super.xxxx
        }

    }

    public long getContentLength() {
        return mContentLength;
    }

    private class Android4JsInterface {

        /**
         * 焦点获取监听
         */
        @JavascriptInterface
        public void setViewEnabled(boolean enabled) {
            if (mOnFocusChangeListener != null) {
                mOnFocusChangeListener.onFocusChange(enabled);
            }
        }

        /**
         * 文字字数监听器
         */
        @JavascriptInterface
        public void staticWords(long num) {
            mContentLength = num;
            if (mOnTextLengthChangeListener != null) {
                mOnTextLengthChangeListener.onTextLengthChange(num);
            }
        }

        /**
         * 获取标题和内容
         */
        @JavascriptInterface
        public void getTitleAndHtml(String title, String htmljson) {
            if (mOnOutHandleListener != null) {
                mOnOutHandleListener.onGetTitleContent(title, htmljson);
            }
        }

        /**
         * 点击了封面图
         */
        @JavascriptInterface
        public void clickHeaderImage() {
            if (mOnOutHandleListener != null) {
                mOnOutHandleListener.onClickHeaderImageListener();
            }
        }
    }


    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // still not support RTL.
        setPadding(start, top, end, bottom);
    }

    public void setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }


    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    /**
     * 加载css
     */
    public void loadCSS(String cssFile) {
        String jsCSSImport = "(function() {" +
                "    var head  = document.getElementsByTagName(\"head\")[0];" +
                "    var link  = document.createElement(\"link\");" +
                "    link.rel  = \"stylesheet\";" +
                "    link.type = \"text/css\";" +
                "    link.href = \"" + cssFile + "\";" +
                "    link.media = \"all\";" +
                "    head.appendChild(link);" +
                "}) ();";
        exec("javascript:" + jsCSSImport + "");
    }

    public void undo() {
        exec("javascript:RE.exec('undo');");
    }

    public void redo() {
        exec("javascript:RE.exec('redo');");
    }


    /**
     * 加粗
     */
    public void setBold() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.exec('bold');");
    }


    /**
     * 斜体
     */
    public void setItalic() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.exec('italic');");
    }

    public void setStrikeThrough() {
        exec("javascript:RE.saveRange()");
        exec("javascript:RE.exec('strikethrough');");
    }

    /**
     * h1、h2、、h3、h4
     */
    public void setHeading(int heading, boolean b) {
        exec("javascript:RE.saveRange();");
        if (b) {
            exec("javascript:RE.exec('h" + heading + "')");
        } else {
            exec("javascript:RE.exec('p')");
        }
    }

    /**
     * 插入引用块
     */
    public void setBlockquote(boolean b) {
        exec("javascript:RE.saveRange();");
        if (b) {
            exec("javascript:RE.exec('blockquote')");
        } else {
            exec("javascript:RE.exec('p')");
        }
    }


    public void insertImage(String url, Long id, long width, long height) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.insertImage('" + url + "'," + id + ", " + width + "," + height + ");");
    }

    /**
     * 删除图片
     */
    public void deleteImageById(Long id) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.removeImage(" + id + ");");
    }


    /**
     * 插入链接
     */
    public void insertLink(String href, String title) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.insertLink('" + title + "', '" + href + "');");
    }

    /**
     * 修改链接
     */
    public void changeLink(String href, String title) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.changeLink('" + title + "', '" + href + "');");
    }

    /**
     * 掺入代办事项
     */
    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + System.currentTimeMillis() + "');");
    }

    public void setImageUploadProcess(long id, int process) {
        exec("javascript:RE.changeProcess(" + id + ", " + process + ");");
    }

    public void setImageFailed(long id) {
        exec("javascript:RE.uploadFailure(" + id + ");");
    }

    public void setImageReload(long id) {
        exec("javascript:RE.uploadReload(" + id + ");");
    }

    /**
     * 获取焦点
     */
    public void focusEditor() {
        requestFocus();
    }

    /**
     * 清除焦点
     */
    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }

    /**
     * 添加产品
     */
    public void edAddProduct(Integer id, String bean) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.addpordack('" + id + "', '" + bean + "');");
    }

    /**
     * 添加图片
     */
    public void edAddimgsrc(String imgsrc) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.addimgsrc('" + imgsrc + "');");
        Log.e("imgsrc", imgsrc);
    }

    /**
     * 获取文本信息
     */
    public void edThishtml() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.thishtml();");
    }

    /**
     * 回显
     */
    public void edOutdata(String title, String htmljson) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.outdata('" + title + "', '" + htmljson + "');");
    }

    /**
     * 点击上传封面
     */
    public void edUpimg() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.upimg();");
    }

    /**
     * 传入封面链接
     */
    public void edUpcover(String imgsrc) {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.upcover('" + imgsrc + "');");
    }

    /**
     * 分割线
     */
    public void insertHr() {
        exec("javascript:RE.saveRange();");
        exec("javascript:RE.insertLine();");
    }

    /**
     * 浏览器储存：卸载或清空缓存时会丢失
     */
    public void lStorage() {
        exec("javascript:RE.lStorage();");
    }

    /**
     * 回显
     */
    public void gStorage() {
        exec("javascript:RE.gStorage();");
    }

    /**
     * 清空储存
     */
    public void loclear() {
        exec("javascript:RE.loclear();");
    }
}