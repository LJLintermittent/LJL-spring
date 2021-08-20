package com.learn.myspring.context.annotation;

import java.lang.annotation.*;

/**
 * Description:
 * date: 2021/8/20 13:27
 * Package: com.learn.myspring.context.annotation
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 用于配置作用域的自定义注解，方便通过配置Bean对象注解的时候，拿到Bean的作用域，一般都使用singleton
public @interface Scope {

    String value() default "singleton";

}
