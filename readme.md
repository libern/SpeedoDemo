# Android 动态加载 Jar 文件

### 利用DexClassLoader来实现。

需要将普通的jar转换成dex的jar。

```
cd /Users/libern/Library/Android/sdk/build-tools/29.0.1
./dx --dex --output=dynamic_combine_dx.jar /Users/libern/LibernWorking/dev/android/SpeedoLib/libspeedo/build/libs/libspeedo.jar
```