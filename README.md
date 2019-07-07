# aptilb

## 1. Features(功能)

- 组件化路由
- 注解（注解获取控件，注解onClick）
- aop切片编程

>拓展功能
  -   groovy插件注入
  
## 2. Structure(结构) 
- annotation（注解 java）
- api（编译代码所依赖接口 library）
- complier （编译生成文件 java）
- aop切片包（基础切片功能 library）
- 插件

## 3. Content(内容)

- annotation

   | aop  | apt               |  inject|
   |----- |-------------------| ----| 
   | 切片  |  组件化/注解控件   |注入|

- api

   |  bind |router|
   |-------|-----|
   | 绑定控件  |路由|
   
   - 技术点说明：
     
     1.bind 通过 static UnBinder bind（NonNull T t）初始化，构造函数获取对象
     
     2.router  主要是  AptHub类存储跳转   RealRouter 获取context对象  
     
     
- complier

   | GenerateRouter  |GenerateInterceptor|GenerateInjectParam|
   |---|---|----|
   | 生成路由  |生成拦截器|生成传参|

- aop

   | Async  |Cacheable|HookMethod|LogMethod|NeedPermission|Prefs                | Safe|Trace   |
   |--------|---------|----------|---------|--------------|---------------------|-----|--------|
   |  异步   |缓存     |埋点       |日志     |权限|          SharedPreferences存储 |异常  |追踪耗时|

- 插件

   | aspectj  |inject|router|
   |----------|------|------|
   |  注解     |注入  |路由  |
   
    - 技术点说明：
    
     1.router 通过groovy插件简化使用，配置注入AptHub中路由对象，
     
     `def android = project.extensions.findByName("android")`  只有当app项目依赖的时候，才会把路由对象注入进AptHub
     
     [注入主要代码](./plugin/src/main/groovy/com/flyang/plugin/router/asm/IMethodVisitor.groovy)
     
## 4. Deploy(配置)     

1.gradle 根目录
     
     buildscript {
     repositories {
           maven {
               url("http://127.0.0.1:8081/repository/basic_beta/")
            }
           google()
           mavenCentral()
           jcenter()
       }
       dependencies {
           classpath 'com.android.tools.build:gradle:3.3.1'
           classpath 'com.flyang.common:plugin:2.1.1.2019_beta_01'
           // NOTE: Do not place your application dependencies here; they belong
           // in the individual module build.gradle files
       }
        apply from: 'config.gradle'
     }
     
 配置版本   

    ext {
            apiVersion = "2.1.1.2019_beta_01"
            annotationVersion = "2.1.1.2019_beta_01"
            complierVersion = "2.1.1.2019_beta_01"
        }

2.gradle moudle目录

    import com.flyang.plugin.aspectj.AspectjPlugin
    import com.flyang.plugin.router.RouterPlugin
    import com.flyang.plugin.inject.InjectPlugin
    
    apply plugin: AspectjPlugin
    apply plugin: RouterPlugin
    apply plugin: InjectPlugin
    
   说明：如果只单使用路由，只配置路由插件即可，如果还用切片配置AspectjPlugin和 
   
    api 'com.flyang.common:aop:2.1.1.2019_beta_01'

 
## 4. Warn(提醒)   
   目前还在开发中，还不是完善框架，固还未在jitpack发布完善版本，所有的使用目前只是用的搭建的本地nexus
   
   
## 分包构想：

- basiclib
    
        1.basic
        2.util
    
- aptlib

        1.annotaton
        2.aop
        3.api
        4.complier
        
- vielib

        1. 基础控件（侧滑关闭加载activity，动画，刷新控件，自动换行布局等）
           
        
- netlib

        1.imageloader
        2.network
    
    其中util基本上全局都依赖，降低其他部分耦合度
   







