package com.learn.myspring.aop.aspectj;

import com.learn.myspring.aop.Pointcut;
import com.learn.myspring.aop.PointcutAdvisor;
import org.aopalliance.aop.Advice;

/**
 * Description:
 * date: 2021/8/19 17:37
 * Package: com.learn.myspring.aop.aspectj
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// AspectJExpressionPointcutAdvisor 实现了 PointcutAdvisor 接口，把切面 pointcut、
// 拦截方法 advice 和具体的拦截表达式包装在一起。
// 这样就可以在xml的配置中定义一个pointcutAdvisor切面拦截器了
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

    // 切面
    private AspectJExpressionPointcut pointcut;

    // 具体的拦截方法
    private Advice advice;

    // 表达式
    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public Pointcut getPointcut() {
        if (null == pointcut) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

}
