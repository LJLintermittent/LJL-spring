package com.learn.myspring.aop;

/**
 * Description:
 * date: 2021/8/18 17:30
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 切入点接口，定义用于获取 ClassFilter、MethodMatcher 的两个类，这两个接口获取的都是切点表达式提供的内容
public interface Pointcut {

    /**
     * Return the ClassFilter for this pointcut.
     * @return the ClassFilter (never <code>null</code>)
     */
    ClassFilter getClassFilter();

    /**
     * Return the MethodMatcher for this pointcut.
     * @return the MethodMatcher (never <code>null</code>)
     */
    MethodMatcher getMethodMatcher();

}
