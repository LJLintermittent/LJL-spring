package com.learn.test.bean.unit16;

import com.learn.myspring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Description:
 * date: 2021/8/23 15:21
 * Package: com.learn.test.bean.unit16
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class UserServiceBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("拦截方法：" + method.getName());
    }
}
