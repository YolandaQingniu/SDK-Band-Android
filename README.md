# 轻牛蓝牙Android SDK 

## 快速集成 
### 混淆配置(proguard-rules)
+ -keep class com.qingniu.scale.model.BleScaleData{*;}

##具体操作文档
[具体集成说明文档](https://yolandaqingniu.github.io/zh/android/)

### Android Studio
* 在你工程的根目录下的 **build.gradle**添加**jitpack**支持
   ```
   allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
   ```
* 在你的module的根目录下的**build.gradle**添加依赖
	```
	<!--这里的版本号，1.0.0-beta 可以指定为任意release版本-->
	<!--如果希望一直使用最新版本可以替换 1.0.0-beta3= 为 master-SNAPSHOT -->
	dependencies {
	        ...
	        compile 'com.github.YolandaQingniu:SDK-Band-Android:1.0.0-beta'
	}
	```
	
### Eclipse
* 下载最新的[jar和so库](https://github.com/YolandaQingniu/SDK-Band-Android/releases/download/1.0.0-beta/qnwristsdk-1.0.0-beta-Android.zip)，导入下载的`jar和so库`
* 在清单文件中申请蓝牙权限、位置权限、网络权限（离线SDK不需要）
    ```
   <!--蓝牙权限-->
   <uses-permission android:name="android.permission.BLUETOOTH" />
   <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
   <!--6.0及之后需要动态申请-->
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <!--用来存储日志-->
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   <!--如果是在线的sdk需要网络权限-->
   <uses-permission android:name="android.permission.INTERNET" />
   <!-- 前台服务权限，防止应用退到后台手环断开连接-->
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>


    ```
* 需要在**AndroidManifest.xml**注册SDK中的组件：
 	``` 
   <service android:name="com.qingniu.qnble.scanner.BleScanService"/>
   <service android:name="com.qingniu.wrist.ble.WristBleService"/>
   ```   
* SDK中使用到了v4包的资源，开发者项目中需要引入v4包的资源

## 注意事项
- targetSdkVersion 在23及以上，需要先获取定位权限，才能扫描到设备，需要开发者自己申请
- 部分手机上使用蓝牙功能需要开启GPS才能扫描到设备，SDK中会输出GPS未开启的日志，但不会回调错误，开发者可以自主进行限制
- 如果你的项目是多进程的，建议限制在主进程才进行SDK的初始化

## 常见问题
具体使用参考[API文档](https://yolandaqingniu.github.io/)和`Demo`，以下为一些常见问题。

1. 初始化提示appid错误
    + 检查初始化文件和使用的appid是否匹配
    + 检查引入的SDK是否是最新的
2. 扫描设备调用成功，但是一直没有设备回调，且无错误回调
    + 检查所扫描的设备，是否已经被其他人连接
    + 部分手机需要开启GPS才能扫描到设备，请检查手机GPS是否开启
3. 连接设备一直无法成功或者成功后很快就断开连接
    + 检查设备是否被其他人连接了
    + 在系统蓝牙中查看是否当前连接的设备已经被配对,如果已经配对，需要取消配对
    + 部分手机需要先扫描才能连接成功，先扫描设备再进行连接
4. SDK返回无定位权限错误
    + 检查是否对**ACCESS_COARSE_LOCATION**和**ACCESS_FINE_LOCATION**都进行了申请，SDK中对2个权限都进行了校验
    + 是否编译版本26以及以上，如果是，2个权限都需要单独申请(8.0的新特性)

**`提示`**：遇到无法定位的问题，希望开发者能第一时间提供日志，以便我们尽快找到问题    
