package com.learn.myspring.beans.factory.config;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;

/**
 * Description:
 * date: 2021/8/10 14:24
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//允许自定义修改 BeanDefinition 属性信息
public interface BeanFactoryPostProcessor {

    /**
     * 在所有的 BeanDefinition 加载完成后，实例化 Bean 对象之前，提供修改 BeanDefinition 属性的机制
     *
     * @param beanFactory
     * @throws BeansException
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
