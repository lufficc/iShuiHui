# 鼠绘漫画非官方客户端

#### 一个非官方的鼠绘漫画，是一个练手的小项目，但涵盖android开发的各个方面，包括Retrofit请求数据，谷歌Gson,Okhttp自定义Interceptor,错误处理，视图的状态切换，Fragment平缓切换及缓存，不依赖第三方实现简单的加载更多，webview实现NestedScroll，简单的mvp模式，集成友盟统计和Fir.im版本更新等等，总之是一个适合新手参考的案例。

#### 另外，本项目用到了自己的另一个多管理状态的layout,欢迎参考源码[lufficc/StateLayout](https://github.com/lufficc/StateLayout)


#### 依赖（不包括Google Support）

1.    [glide](https://github.com/bumptech/glide)
    compile 'com.squareup.retrofit2:retrofit:2.1.0'
    compile 'com.google.code.gson:gson:2.7'
    compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    compile 'com.jakewharton:butterknife:8.2.1'
    apt 'com.jakewharton:butterknife-compiler:8.2.1'
    compile 'org.greenrobot:eventbus:3.0.0'
    compile 'com.github.shem8:material-login:1.4.0'
    compile('com.github.ozodrukh:CircularReveal:1.3.1@aar') {
        transitive = true;
    }
    compile 'de.hdodenhof:circleimageview:2.1.0'
    compile 'com.miguelcatalan:materialsearchview:1.4.0'
    compile 'com.umeng.analytics:analytics:latest.integration'

    compile 'com.lufficc:stateLayout:0.0.5'


### 截图：

![sample](screenshots/device-2016-08-27-230634.gif)
![sample](screenshots/device-2016-08-27-233253.gif)
![sample](screenshots/device-2016-08-27-233759.gif)
![sample](screenshots/device-2016-08-27-234239.gif)
![sample](screenshots/device-2016-08-27-234499.png)


## 有好的意见或者建议欢迎Issues,我的网站 [https://lufficc.com](https://lufficc.com)

# License
	Copyright 2015 Miguel Catalan Bañuls

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.