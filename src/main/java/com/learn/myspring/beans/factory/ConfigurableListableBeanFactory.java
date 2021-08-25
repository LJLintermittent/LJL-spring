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

    /*
      ConfigurableListableBeanFactory集成了三大接口的职能以外，还扩展了修改Bean定义信息和分析Bean的功能
      并且实现了预实例化单例Bean的功能，这个非常重要，在实例化单例Bean的时候，需要涉及对循环依赖的处理
     */

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    void preInstantiateSingletons() throws BeansException;

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
}
