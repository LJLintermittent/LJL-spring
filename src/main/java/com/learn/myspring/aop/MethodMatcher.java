package com.learn.myspring.aop;

import java.lang.reflect.Method;

/**
 * Description:
 * date: 2021/8/18 17:31
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 方法匹配，找到表达式范围内匹配下的目标类和方法
public interface MethodMatcher {

    /**
     * Perform static checking whether the given method matches. If this
     * @return whether or not this method matches statically
     */
    boolean matches(Method method, Class<?> targetClass);

}
