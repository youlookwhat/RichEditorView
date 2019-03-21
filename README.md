## RichEditorView
Android 基于WebView的富文本编辑器 - 仿简书编辑器

## Supported Functions

 - Bold
 - Italic
 - Strikethrough
 - Blockquote
 - Heading 1
 - Heading 2
 - Heading 3
 - Heading 4
 - Line
 - Undo
 - Redo
 - Insert Image
 - Insert Product(Custom layout)

特点：

 - 点击内容部分，键盘会将底部菜单栏弹起，点击标题则收起
 - 文字会自动定位在菜单栏的上方
 - 文字类型标识如加粗，删除线会根据光标位置自动改变

## Setting for Editor

获取标题和内容:

```java
richEditor.edThishtml();
```

添加封面图片:

```java
richEditor.edUpcover(headerImageSrc);
```


添加内容图片:

```java
richEditor.edAddimgsrc(contentImageSrc);
```

添加产品：

```java
richEditor.edAddProduct(123, new Gson().toJson(goodsBean));
```

回显内容：

```java
richEditor.edOutdata(mTitle, mContent);
```

## Screenshots

<img width="300" height=“470” src="https://github.com/youlookwhat/RichEditorView/blob/master/file/richeditor.jpeg"></img>
<img width="300" height=“470” src="https://github.com/youlookwhat/RichEditorView/blob/master/file/richeditor.gif"></img>


## Project Origin
为什么会有此开源项目？

公司准备在自己App内做编辑器模块，本来想用百度的开源编辑器，但是功能太复杂，参考了很多类似的编辑器都达不到项目需要的效果。结果发现了``RichEditorWeb``，基本功能都满足，就在此项目中做了修改，并运用到自己的项目中。感谢此项目的提供者。

在移植到项目中时，出现很多问题(如键盘上面的布局不弹起)，为了解决这些问题并在其基础上扩展功能 花费了大量时间。在此特地将编辑器从项目中剥离出来，供后来的朋友参考借鉴。
如有帮助，还请Star一下，支持一下作者，非常感谢~



## Thanks
 - reference by [RichEditorWeb](https://github.com/dengdaoyus/RichEditorWeb)

## License
```
Copyright (C) 2016 Bin Jing

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```