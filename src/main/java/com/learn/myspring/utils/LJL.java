package com.learn.myspring.utils;

import java.lang.annotation.*;

/**
 * Description:
 * date: 2021/9/17 12:34
 * Package: com.learn.myspring.utils
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER,ElementType.TYPE})
@Documented
@Inherited
public @interface LJL {

}
