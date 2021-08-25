package com.learn.myspring.beans.factory.config;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.BeanFactory;

/**
 * Description:
 * date: 2021/8/10 14:24
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface AutowireCapableBeanFactory extends BeanFactory {

    /*
      AutowireCapableBeanFactory提供了Bean对象的创建，注入职能，并且提供了对Bean初始化前后的扩展性处理职能
      主要职责是处理在当前工厂中注册的Bean实例并使其达到可用状态
      在本项目里，主要是定义Bean初始化前后应用BeanPostProcessors的方法
     */

    /**
     * 执行 BeanPostProcessors 接口实现类的 postProcessBeforeInitialization 方法
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    /**
     * 执行 BeanPostProcessors 接口实现类的 postProcessorsAfterInitialization 方法
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;
}
