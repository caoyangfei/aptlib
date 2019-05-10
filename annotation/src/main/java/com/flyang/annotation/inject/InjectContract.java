package com.flyang.annotation.inject;

/**
 * @author caoyangfei
 * @ClassName InjectContract
 * @date 2019/5/6
 * ------------- Description -------------
 * 注入实现接口
 */
public interface InjectContract {

    /**
     * "@Inject" 注解标示的class 最终都会注入到该"@IMethod"注解标示过的方法中
     * 注："@IMethod"注解标示过的方法将由编译器自动注入实现代码，注入最终的代码如下如：
     *
     * @param className class name
     * @IMethod public void iMethodName() {
     * injectClass("injectClassName1")
     * injectClass("injectClassName2")
     * injectClass("injectClassName3")
     * injectClass("injectClassName4")
     * }
     * <p>
     * 用户可以在该方法中通过反射完成自己的业务需求
     */
    void injectClass(String className);
}
