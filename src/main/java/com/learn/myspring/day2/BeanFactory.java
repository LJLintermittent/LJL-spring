package com.learn.myspring.day2;

/**
 * Description:
 * date: 2021/8/5 18:51
 * Package: com.learn.myspring.day2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface BeanFactory {

    Object getBean(String name) throws BeansException;

}
