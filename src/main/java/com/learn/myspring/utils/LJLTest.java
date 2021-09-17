package com.learn.myspring.utils;

import java.lang.reflect.Method;

/**
 * Description:
 * date: 2021/9/17 12:36
 * Package: com.learn.myspring.utils
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class LJLTest {

    @LJL
    public static void main(String[] args) throws NoSuchMethodException {
        Class<?> clazz = LJLTest.class;
        Method method = clazz.getMethod("main", String[].class);
        LJL annotation = method.getAnnotation(LJL.class);

    }

}
