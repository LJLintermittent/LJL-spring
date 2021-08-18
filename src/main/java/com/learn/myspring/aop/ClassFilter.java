package com.learn.myspring.aop;

/**
 * Description:
 * date: 2021/8/18 17:30
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 定义类匹配类，用于切点找到给定的接口和目标类
public interface ClassFilter {

    /**
     * Should the pointcut apply to the given interface or target class?
     * @param clazz the candidate target class
     * @return whether the advice should apply to the given target class
     */
    boolean matches(Class<?> clazz);
}
