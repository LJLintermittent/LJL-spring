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

    /**
     * 三级缓存的核心处理
     * 这两个方法主要玩的就是三级缓存之间的变换
     */

    // 获取单例Bean
    Object getSingleton(String name);

    // 注册单例Bean
    void registerSingleton(String beanName, Object singletonObject);

}
