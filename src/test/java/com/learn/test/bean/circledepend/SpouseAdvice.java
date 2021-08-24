package com.learn.test.bean.circledepend;

import com.learn.myspring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Description:
 * date: 2021/8/24 18:50
 * Package: com.learn.test.bean.circledepend
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class SpouseAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("关怀小两口(切面)：" + method);
    }
}
