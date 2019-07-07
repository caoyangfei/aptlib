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
     
     [注入主要代码](.aptlib/plugin/src/main/groovy/com/flyang/plugin/router/asm/IMethodVisitor.groovy)
     
     














