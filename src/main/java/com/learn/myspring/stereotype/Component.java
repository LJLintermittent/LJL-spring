package com.learn.myspring.stereotype;

import java.lang.annotation.*;

/**
 * Description:
 * date: 2021/8/20 13:28
 * Package: com.learn.myspring.stereotype
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String value() default "";

}
