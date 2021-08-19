package com.learn.myspring.aop;

/**
 * Description:
 * date: 2021/8/19 17:37
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public interface PointcutAdvisor extends Advisor{

    /**
     * Get the Pointcut that drives this advisor.
     */
    Pointcut getPointcut();

}
