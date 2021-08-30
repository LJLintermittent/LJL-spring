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

    /*
      spring AOP 是以Advisor为核心模型使用动态代理技术实现，spring的事务控制就是使用AOP实现的，保证了声明即生效的效果
      spring代理都是依赖advisor实现的，方法匹配和类匹配的职责都是在pointcut接口的实现类中聚合，从而实现advice和pointcut的
      职责清晰，也正是因为这种设计，才完成了spring aop模块与著名aspectj框架的结合，最终实现了基于织入点语法的切面配置，同时
      提供了方法调用前，方法调用后，异常时调用出发等场景
     */

    /**
     * Return the advice part of this aspect. An advice may be an
     * interceptor, a before advice, a throws advice, etc.
     *
     * @return the advice that should apply if the pointcut matches
     * @see org.aopalliance.intercept.MethodInterceptor
     * @see BeforeAdvice
     */
    Advice getAdvice();

}
