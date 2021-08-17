package com.learn.myspring.beans.factory.config;

/**
 * Description:
 * date: 2021/8/5 18:43
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface SingletonBeanRegistry {

    //获取单例对象
    Object getSingleton(String name);

    void registerSingleton(String beanName, Object singletonObject);

}
