'use strict';
'use struct';

function HashMap() {
    /** Map 大小 **/
    var size = 0;
    /** 对象 **/
    var entry = new Object();

    /** 存 **/
    this.put = function(key, value) {
        if (!this.containsKey(key)) {
            size++;
        }
        entry[key] = value;
    }

    /** 取 **/
    this.get = function(key) {
        if (this.containsKey(key)) {
            return entry[key];
        } else {
            return null;
        }
    }

    /** 删除 **/
    this.remove = function(key) {
        if (delete entry[key]) {
            size--;
        }
    }

    /** 是否包含 Key **/
    this.containsKey = function(key) {
        return (key in entry);
    }

    /** 是否包含 Value **/
    this.containsValue = function(value) {
        for (var prop in entry) {
            if (entry[prop] == value) {
                return true;
            }
        }
        return false;
    }

    /** 所有 Value **/
    this.values = function() {
        var values = new Array(size);
        for (var prop in entry) {
            values.push(entry[prop]);
        }
        return values;
    }

    /** 所有 Key **/
    this.keys = function() {
        var keys = new Array(size);
        for (var prop in entry) {
            keys.push(prop);
        }
        return keys;
    }

    /** Map Size **/
    this.size = function() {
        return size;
    }
}

var RE = {
    currentRange: {
        startContainer: null,
        startOffset: 0,
        endContainer: null,
        endOffset: 0
    },
    cache: {
        editor: null,
        title: null,
        currentLink: null,
        line: null
    },
    /**
     * 当前需要的按钮在这里设置
     */
    commandSet: ['bold', 'italic', 'strikethrough', 'redo', 'undo'],
    /**
     * 初始化的配置信息 不用管
     */
    schemeCache: {
        FOCUS_SCHEME: 'focus://',
        CHANGE_SCHEME: 'change://',
        STATE_SCHEME: 'state://',
        CALLBACK_SCHEME: 'callback://',
        IMAGE_SCHEME: 'image://'
    },
    setting: {
        screenWidth: 0,
        margin: 20
    },
    imageCache: new HashMap(),
    init: function init() {
        //初始化内部变量
        var _self = this;
        _self.initCache();
        _self.initSetting();
        _self.bind();
        _self.focus();
    },


    /**
     * 事件绑定  
     * 包括失去焦点  获得焦点
     */
    bind: function bind() {
        var _self = this;

        var _self$schemeCache = _self.schemeCache,
            FOCUS_SCHEME = _self$schemeCache.FOCUS_SCHEME,
            STATE_SCHEME = _self$schemeCache.STATE_SCHEME,
            CALLBACK_SCHEME = _self$schemeCache.CALLBACK_SCHEME;


        document.addEventListener('selectionchange', _self.saveRange, false);

        _self.cache.title.addEventListener('focus', function() {
            window.AndroidInterface.setViewEnabled(true);
        }, false);

        // 获取焦点
        _self.cache.editor.addEventListener('focus', function() {
            // 输入时获取html的内容，length = 0添加p标签
            if (_self.cache.editor.innerHTML.length == 0) {
                _self.cache.editor.innerHTML = "<p><br></p>"
            }
        }, false);

        _self.cache.title.addEventListener('blur', function() {
            window.AndroidInterface.setViewEnabled(false);
        }, false);

        // 失去焦点时
        _self.cache.editor.addEventListener('blur', function() {
            _self.saveRange();
            // 如果只有"<p><br></p>"则作为无文本处理
            if (_self.cache.editor.innerHTML == "<p><br></p>") {
                _self.cache.editor.innerHTML = ""
            }
        }, false);

        _self.cache.editor.addEventListener('click', function(evt) {
            _self.saveRange();
            _self.getEditItem(evt);
        }, false);

        _self.cache.editor.addEventListener('keyup', function(evt) {
            // 输入时获取html的内容，length = 0添加p标签
            if (_self.cache.editor.innerHTML.length == 0) {
                _self.cache.editor.innerHTML = "<p><br></p>"
            }
            if (evt.which == 37 || evt.which == 39 || evt.which == 13 || evt.which == 8) {
                _self.getEditItem(evt);
            }
        }, false);

        _self.cache.editor.addEventListener('input', function() {
            AndroidInterface.staticWords(_self.staticWords());
        }, false);
    },

    /**
     * 设置当前浏览器的最小高度
     */
    initCache: function initCache() {
        var _self = this;
        _self.cache.editor = document.getElementById('editor');
        _self.cache.title = document.getElementById('title');
        _self.cache.line = document.getElementsByClassName('line')[0];
        _self.cache.editor.style.minHeight = window.innerHeight - 69 + 'px';
    },
    /**
     * 监听当前浏览器的宽度
     */
    initSetting: function initSetting() {
        var _self = this;
        _self.setting.screenWidth = window.innerWidth - 20;
    },


    /**
     * 获得焦点事件
     */
    focus: function focus() {
        //聚焦
        var _self = this;
        var range = document.createRange();
        range.selectNodeContents(this.cache.editor);
        range.collapse(false);
        var select = window.getSelection();
        select.removeAllRanges();
        select.addRange(range);
        _self.cache.editor.focus();
    },

    /**
     * 获取html片段
     */
    getHtml: function getHtml() {
        var _self = this;
        return _self.cache.editor.innerHTML;
    },
    staticWords: function staticWords() {
        var _self = this;
        var content = _self.cache.editor.innerHTML.replace(/<div\sclass="tips">.*<\/div>|<\/?[^>]*>/g, '').replace(/\s+/, '').trim();
        return content.length;
    },
    saveRange: function saveRange() {
        //保存节点位置
        var _self = this;
        var selection = window.getSelection();
        if (selection.rangeCount > 0) {
            var range = selection.getRangeAt(0);
            var startContainer = range.startContainer,
                startOffset = range.startOffset,
                endContainer = range.endContainer,
                endOffset = range.endOffset;

            _self.currentRange = {
                startContainer: startContainer,
                startOffset: startOffset,
                endContainer: endContainer,
                endOffset: endOffset
            };
        }
    },
    // 标题字数监听
    inputlength: function inputlength(value) {
//        console.log(value)
        document.getElementById("idlength").innerText = value.length
        if (value.length >= 15) {
             document.getElementById("idlengthbox").style.color="red"
             document.getElementById("idlengthbox").style.textAlign="right"
        } else {
            document.getElementById("idlengthbox").style.color="#999"
            document.getElementById("idlengthbox").style.textAlign="right"
        }
    },
    reduceRange: function reduceRange() {
        //还原节点位置
        var _self = this;
        var _self$currentRange = _self.currentRange,
            startContainer = _self$currentRange.startContainer,
            startOffset = _self$currentRange.startOffset,
            endContainer = _self$currentRange.endContainer,
            endOffset = _self$currentRange.endOffset;

        var range = document.createRange();
        var selection = window.getSelection();
        selection.removeAllRanges();
        range.setStart(startContainer, startOffset);
        range.setEnd(endContainer, endOffset);
        selection.addRange(range);
    },
    exec: function exec(command) {
        //执行指令
        var _self = this;
        if (_self.commandSet.indexOf(command) !== -1) {
            document.execCommand(command, false, null);
        } else {
            var value = '<' + command + '>';
            document.execCommand('formatBlock', false, value);
            _self.getEditItem({});
        }
    },
    /**
     * 获取当前的所有状态
     * @param {*} evt 
     */
    getEditItem: function getEditItem(evt) {
        //通过点击时，去获得一个当前位置的所有状态
        console.log(evt)
        var _self = this;
        var _self$schemeCache2 = _self.schemeCache,
            STATE_SCHEME = _self$schemeCache2.STATE_SCHEME,
            CHANGE_SCHEME = _self$schemeCache2.CHANGE_SCHEME,
            IMAGE_SCHEME = _self$schemeCache2.IMAGE_SCHEME;

        if (evt.target && evt.target.tagName === 'A') {
            _self.cache.currentLink = evt.target;
            var name = evt.target.innerText;
            var href = evt.target.getAttribute('href');
            window.location.href = CHANGE_SCHEME + encodeURI(name + '@_@' + href);
        } else {
            if (evt.which == 8) {
                AndroidInterface.staticWords(_self.staticWords());
            }
            var items = [];
            _self.commandSet.forEach(function(item) {
                if (document.queryCommandState(item)) {
                    items.push(item);
                }
            });
            if (document.queryCommandValue('formatBlock')) {
                items.push(document.queryCommandValue('formatBlock'));
            }
            window.location.href = STATE_SCHEME + encodeURI(items.join(','));
        }
    },

    /**
     * 添加产品
     * 注意：之前一直回显不了。将 htmls 缩为一行，和 mid="${res.mid}" 改为双引号就好了。
     */
    addpordack: function addpordack(id, datas) {
        var res = JSON.parse(datas)
        var htmls = '<p class="add"><br></p><div contenteditable="false" class="goodspage" mid='+res.mid+' goodsid='+id+' style="padding: 0.5rem 0;display:flex;width:100%;margin-top:1rem;position: relative;box-shadow: 0px 2px 10px 1px #d2d1d1;margin-bottom:1rem"><p onclick="this.parentNode.parentNode.removeChild(this.parentNode);console.log(this.parentNode)" contenteditable="false" class="rem"><img style="width: 2rem;" src="https://img0.bevol.cn/UploadFile/find/0_3120c680-92ff-40d8-a01e-b09633f25f0d.png"></p><div contenteditable="false" goodsid='+id+'><img contenteditable="false" src='+res.imageSrc+' alt="" style="width: 6rem;margin-left: 0.5rem;"></div><div mid='+res.mid+' goodsid='+id+' style="width: 16rem;display: flex;flex-direction: column;justify-content: space-around;margin-left: 0.5rem;height:6rem;overflow: hidden;"><div contenteditable="false" style="margin-top:0rem;font-size:1rem;overflow: hidden;text-overflow:ellipsis;display: -webkit-box;-webkit-line-clamp: 2;-webkit-box-orient: vertical;">'+res.title+'</div><div contenteditable="false" style="margin-top:0rem;font-size:14px;overflow: hidden;text-overflow:ellipsis;display: -webkit-box;-webkit-line-clamp: 1;-webkit-box-orient: vertical;color:#999">'+res.alias+'</div></div></div></div><p class="add"><br></p>'
        document.execCommand('insertHtml', true, htmls);
        document.getElementById("editor").innerHTML = document.getElementById("editor").innerHTML.replace(/class="goodspage"/g, 'contentEditable="false" class="goodspage"')
        document.getElementById("editor").innerHTML = document.getElementById("editor").innerHTML.replace(/style="padding: 0.5rem 0;display:flex;width:100%;margin-top:1rem;position: relative;box-shadow: 0px 2px 10px 1px #d2d1d1;margin-bottom:1rem">/g, 'style="padding: 0.5rem 0;display:flex;width:100%;margin-top:1rem;position: relative;box-shadow: 0px 2px 10px 1px #d2d1d1;margin-bottom:1rem"><div contenteditable="false" onclick="this.parentNode.parentNode.removeChild(this.parentNode)" contenteditable="false" class="rem" style=""><img contenteditable="false" style="width: 2rem;" src="https://img0.bevol.cn/UploadFile/find/0_3120c680-92ff-40d8-a01e-b09633f25f0d.png"></div>')
        this.init()
        this.getEditItem()
    },

    /**
     * 添加图片
     */
    addimgsrc: function addimgsrc(imgsrc2) {

        //var imgsrc = 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542190063251&di=1d6c3d89a7e8b57450065ec17b15ab4c&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fback_pic%2Fqk%2Fback_origin_pic%2F00%2F04%2F43%2F570fcd6809ce6f032a8e96ca480b7def.jpg'
        var imgsrc = imgsrc2
        var img = new Image()
        img.src = imgsrc
        img.onload = function() {

            if (this.width > window.innerWidth) {
                this.width = 100
                var htmls='<p class="add"><br></p><div class="maximgbox" style="width:100%;overflow: hidden;position:relative;"  contenteditable="false"><p onclick="this.parentNode.parentNode.removeChild(this.parentNode);console.log(this.parentNode)" contenteditable="false" class="rem"><img style="width: 2rem;" src="https://img0.bevol.cn/UploadFile/find/0_3120c680-92ff-40d8-a01e-b09633f25f0d.png"></p><img src=' +imgsrc+ ' style="width: 100%;"></div><p class="add"><br></p>'
            } else {
                var htmls='<p class="add"><br></p><div class="imgbox" style="overflow: hidden;;text-align:center;position:relative;"  contenteditable="false"><p onclick="this.parentNode.parentNode.removeChild(this.parentNode);console.log(this.parentNode)" contenteditable="false" class="rem"><img style="width: 2rem;" src="https://img0.bevol.cn/UploadFile/find/0_3120c680-92ff-40d8-a01e-b09633f25f0d.png"></p><img src='+imgsrc+' style=" max-width: 100%;"></div><p class="add"><br></p>'
            }
            document.execCommand('insertHtml', false, htmls)
            this.init()
            this.getEditItem()
        }
    },

    /**
     * 兼容Android6.0 删除图片不能删除“✘图片”的问题；缺点是添加图片后没有了光标
     */
    addLineLast: function addLineLast() {
         document.getElementById("editor").innerHTML = document.getElementById("editor").innerHTML.replace(/class="maximgbox"/g, 'contentEditable="false" class="maximgbox"')
         document.getElementById("editor").innerHTML = document.getElementById("editor").innerHTML.replace(/class="imgbox"/g, 'contentEditable="false" class="imgbox"')
    },

    /**
     * 点击按钮更换封面
     */
    upimg: function upimg() {
        //        alert("点击了图片")
        AndroidInterface.clickHeaderImage();
    },

    /**
     * 替换封面
     * @param {*} imgsrc 
     */
    upcover: function upcover(imgsrc2) {
        //        var imgsrc = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542190063251&di=1d6c3d89a7e8b57450065ec17b15ab4c&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fback_pic%2Fqk%2Fback_origin_pic%2F00%2F04%2F43%2F570fcd6809ce6f032a8e96ca480b7def.jpg"
        var imgsrc = imgsrc2

        var imgs = document.getElementById("cover")
        imgs.src = imgsrc;
        document.getElementById("mc").style.cssText = 'position:absolute;background: #4646465e;top:0;display:block;width:100%;min-height:120px;height:'+imgs.height+'px;'
        setTimeout(function() {
                    document.getElementById("mc").style.cssText = 'position:absolute;background: #4646465e;top:0;display:block;width:100%;min-height:120px;height:'+imgs.height+'px;'
                }, 50)
        document.getElementById('cover').onload = function(e) {
            document.getElementById("mc").style.cssText = 'position:absolute;background: #4646465e;top:0;display:block;width:100%;min-height:120px;height:'+imgs.height+'px;'
        }


    },


    /**
    /**
     * 回显示，用此回显还有问题
     */
    outdata: function outdata(title, htmljson) {
        document.getElementsByClassName("title")[0].value = title
        this.inputlength(title)
        document.getElementById("editor").innerHTML = htmljson
    },


    /**
     * 获取文本信息
     * 错误的：onclick="Getinfo(@item.email);"
     * 正确的：onclick="Getinfo('@item.email');"
     */
    thishtml: function thishtml() {
        var title = document.getElementsByClassName("title")[0].value
        var htmljson = document.getElementById("editor").innerHTML
//        console.log(document.getElementById("editor").innerHTML)
        AndroidInterface.getTitleAndHtml(title, htmljson);
    },



    /**
     * 添加html代码
     * @param {*} html 
     */
    insertHtml: function insertHtml(html) {
        // html = "ertHtmlinsertHtmlinsertHtmlinsertHtml"
        var _self = this;
        document.execCommand('insertHtml', false, html);
    },


    /**
     * 设置背景颜色
     * @param {s} color 
     */
    setBackgroundColor: function setBackgroundColor(color) {
        var _self = this;
        document.body.style.backgroundColor = color;
    },
    /**
     * 设置字体颜色
     */
    setFontColor: function setFontColor(color) {
        document.body.style.color = color;
    },
    /**
     * 设置边框颜色
     * @param {} color 
     */
    setLineColor: function setLineColor(color) {
        var _self = this;
        _self.cache.editor.style.borderColor = color;
    },


    /**
     * 添加横线(hr)的
     */
    insertLine: function insertLine() {
        var _self = this;
        var html = '<hr style="color:#F2F2F2"><p class="add"><br></p>';
        _self.insertHtml(html);
        _self.getEditItem({});
    },


    /**
     * 添加超链接啊
     * @param {标题} name 
     * @param {链接} url 
     */
    insertLink: function insertLink(name, url) {
        var _self = this;
        var html = '<a href="' + url + '" class="editor-link">' + name + '</a>';
        _self.insertHtml(html);
    },


    changeLink: function changeLink(name, url) {
        var _self = this;
        var current = _self.cache.currentLink;
        var len = name.length;
        current.innerText = name;
        current.setAttribute('href', url);
        var selection = window.getSelection();
        var range = selection.getRangeAt(0).cloneRange();
        var _self$currentRange2 = _self.currentRange,
            startContainer = _self$currentRange2.startContainer,
            endContainer = _self$currentRange2.endContainer;

        selection.removeAllRanges();
        range.setStart(startContainer, len);
        range.setEnd(endContainer, len);
        selection.addRange(range);
    },


    /**
     * 设置
     * @param {*} url 
     * @param {*} id 
     * @param {*} width 
     * @param {*} height 
     */
    insertImage: function insertImage(url, id, width, height) {
        var _self = this;
        var newWidth = 0,
            newHeight = 0;
        var screenWidth = _self.setting.screenWidth;

        if (width > screenWidth) {
            newWidth = screenWidth;
            newHeight = height * newWidth / width;
        } else {
            newWidth = width;
            newHeight = height;
        }
        var image = '<div><br></div><div class="block">\n\t\t\t\t<div class="img-block"><div style="width: ' + newWidth + 'px" class="process">\n\t\t\t\t\t<div class="fill">\n\t\t\t\t\t</div>\n\t\t\t\t</div>\n\t\t\t\t<img class="images" data-id="' + id + '" style="width: ' + newWidth + 'px; height: ' + newHeight + 'px;" src="' + url + '"/>\n\t\t\t\t<div class="cover" style="width: ' + newWidth + 'px; height: ' + newHeight + 'px"></div>\n\t\t\t\t<div class="delete">\n\t\t\t\t\t<img src="./reload.png">\n\t\t\t\t\t<div class="tips">\u56FE\u7247\u4E0A\u4F20\u5931\u8D25\uFF0C\u8BF7\u70B9\u51FB\u91CD\u8BD5</div>\n\t\t\t\t</div></div>\n\t\t\t\t<input type="text" placeholder="\u8BF7\u8F93\u5165\u56FE\u7247\u540D\u5B57">\n\t\t\t</div><div><br></div>';
        _self.insertHtml(image);
        var img = document.querySelector('img[data-id="' + id + '"]');
        var imgBlock = img.parentNode;
        imgBlock.parentNode.contentEditable = false;
        imgBlock.addEventListener('click', function(e) {
            e.stopPropagation();
            var current = e.currentTarget;
            var img = current.querySelector('.images');
            var id = img.getAttribute('data-id');
            window.location.href = _self.schemeCache.IMAGE_SCHEME + encodeURI(id);
        }, false);
        _self.imageCache.put(id, imgBlock.parentNode);
    },



    changeProcess: function changeProcess(id, process) {
        var _self = this;
        var block = _self.imageCache.get(id);
        var fill = block.querySelector('.fill');
        fill.style.width = process + '%';
        if (process == 100) {
            var cover = block.querySelector('.cover');
            var process = block.querySelector('.process');
            var imgBlock = block.querySelector('.img-block');
            imgBlock.removeChild(cover);
            imgBlock.removeChild(process);
        }
    },


    /**
     * 删除图片
     * @param {*} id 
     */
    removeImage: function removeImage(id) {
        var _self = this;
        var block = _self.imageCache.get(id);
        block.parentNode.removeChild(block);
        _self.imageCache.remove(id);
    },



    uploadFailure: function uploadFailure(id) {
        var _self = this;
        var block = _self.imageCache.get(id);
        var del = block.querySelector('.delete');
        del.style.display = 'block';
    },
    uploadReload: function uploadReload(id) {
        var _self = this;
        var block = _self.imageCache.get(id);
        var del = block.querySelector('.delete');
        del.style.display = 'none';
    },

    /**
     * 储存：卸载或清空缓存时会丢失
     */
    lStorage: function lStorage() {
//        console.log(document.getElementById("cover").src)
//        console.log(document.getElementsByClassName("title")[0].value)
//        console.log(document.getElementById("editor").innerHTML)
        // 封面
        if (document.getElementById("cover").src != "https://img0.bevol.cn/UploadFile/find/0_0d394c19-64d2-48db-93e1-61fc3bfaded1.jpg" && document.getElementById("cover").src != "") {
            localStorage.setItem("cover", document.getElementById("cover").src)
        }
        // 标题
        if (document.getElementsByClassName("title")[0].value != '' && document.getElementsByClassName("title")[0].value.length > 0) {
            localStorage.setItem("title", document.getElementsByClassName("title")[0].value)
        }
        // 内容
        if (document.getElementById("editor").innerHTML != '<p><br></p>') {
            localStorage.setItem("html", document.getElementById("editor").innerHTML)
        }
    },

    /**
     * 回显
     */
    gStorage: function gStorage() {
        // 替换封面，使用注释的代码会没有“更换封面”的按钮
        if (localStorage.getItem("cover")) {
//            document.getElementById("cover").src = localStorage.getItem("cover")
            this.upcover(localStorage.getItem("cover"))
        }
        // 封面，字数提示
        if (localStorage.getItem("title")) {
            document.getElementsByClassName("title")[0].value = localStorage.getItem("title")
            this.inputlength(localStorage.getItem("title"))
        }
        // 回显内容
        if (localStorage.getItem("html")) {
            document.getElementById("editor").innerHTML = localStorage.getItem("html")
        }

    },

    /**
     * 清空储存
     */
    loclear: function loclear() {
        localStorage.clear()
    },


};

RE.init();

/**初始化获取标题焦点*/
window.onload = function() {
    document.getElementsByClassName('title')[0].focus()
}