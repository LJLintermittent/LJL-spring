package com.learn.myspring.aop;

import org.aopalliance.aop.Advice;

/**
 * Description:
 * date: 2021/8/19 17:36
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public interface Advisor {

    /**
     * Return the advice part of this aspect. An advice may be an
     * interceptor, a before advice, a throws advice, etc.
     * @return the advice that should apply if the pointcut matches
     * @see org.aopalliance.intercept.MethodInterceptor
     * @see BeforeAdvice
     */
    Advice getAdvice();

}
