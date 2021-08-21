package com.learn.myspring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * Description:
 * date: 2021/8/21 17:57
 * Package: com.learn.myspring.beans.factory.annotation
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    String value();

}
