package com.learn.myspring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * Description:
 * date: 2021/8/21 17:56
 * Package: com.learn.myspring.beans.factory.annotation
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD,ElementType.FIELD})
public @interface Autowired {

}
