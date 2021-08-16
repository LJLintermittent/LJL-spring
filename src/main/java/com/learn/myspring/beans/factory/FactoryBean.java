package com.learn.myspring.beans.factory;

/**
 * Description:
 * date: 2021/8/16 14:39
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public interface FactoryBean<T> {

    /**
     * 获取对象
     */
    T getObject() throws Exception;

    /**
     * 获取类型
     */
    Class<?> getObjectType();

    /**
     * 判断是否为单例Bean
     */
    boolean isSingleton();

}
