# Android 动态加载 Jar 文件

### 利用DexClassLoader来实现。

jar 项目代码 [https://github.com/libern/SpeedoLib](https://github.com/libern/SpeedoLib)

需要将普通的jar转换成dex的jar。

```
cd /Users/libern/Library/Android/sdk/build-tools/29.0.1
./dx --dex --output=dynamic_combine_dx.jar /Users/libern/LibernWorking/dev/android/SpeedoLib/libspeedo/build/libs/libspeedo.jar
```

1. 模拟下载jar到外部储存目录

2. 从外部加载jar并调用外部方法