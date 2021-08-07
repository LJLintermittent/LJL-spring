package com.learn.myspring.core;

/**
 * Description:
 * date: 2021/8/5 19:13
 * Package: com.learn.myspring.core
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

}
