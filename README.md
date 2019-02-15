## RichEditorView
基于WebView的富文本编辑器 - Android

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

## Demo
gif演示图后期补上

<img width="300" height=“470” src="https://github.com/youlookwhat/RichEditorView/blob/master/file/richeditor.jpeg"></img>


<!--
## Origin
为什么会有此开源项目？

-->


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