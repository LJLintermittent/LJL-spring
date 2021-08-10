package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.config.AutowireCapableBeanFactory;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.beans.factory.config.BeanPostProcessor;
import com.learn.myspring.beans.factory.config.ConfigurableBeanFactory;

/**
 * Description:
 * date: 2021/8/10 14:21
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    void preInstantiateSingletons() throws BeansException;

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}
