package com.learn.myspring.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * Description:
 * date: 2021/8/18 17:37
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 包装切面通知信息
// AdvisedSupport，主要是用于把代理、拦截、匹配的各项属性包装到一个类中，方便在 Proxy 实现类进行使用
// 这和业务开发中包装入参是一个道理，类似与DTO
public class AdvisedSupport {

    /*
      AdvisedSupport主要用于管理advisor
      spring在创建代理的过程中依赖AdvisedSupport，即在执行代理时也需要这个属性，因为创建本身就是为执行做准备的
      从设置的职责来看，无论是jdk代理还是cglib代理都依赖advisor和advice，advice是最小粒度，spring代理都是围绕他们创建的
     */

    // ProxyConfig
    private boolean proxyTargetClass = false;

    // 被代理的目标对象
    private TargetSource targetSource;

    // 方法拦截器
    private MethodInterceptor methodInterceptor;

    // 方法匹配器(检查目标方法是否符合通知条件)
    private MethodMatcher methodMatcher;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }
}
