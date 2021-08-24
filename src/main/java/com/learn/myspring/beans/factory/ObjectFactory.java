package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;

/**
 * Description:
 * date: 2021/8/24 18:07
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public interface ObjectFactory<T> {

    T getObject() throws BeansException;

}


