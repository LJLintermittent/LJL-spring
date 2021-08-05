package com.learn.myspring.day2;

/**
 * Description:
 * date: 2021/8/5 19:10
 * Package: com.learn.myspring.day2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class BeansException extends RuntimeException {

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
