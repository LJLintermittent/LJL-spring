package com.learn.myspring.beans.factory.config;

import com.learn.myspring.beans.BeansException;

/**
 * Description:
 * date: 2021/8/10 14:25
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 对Bean对象扩展的两个接口，也是在使用Spring框架额外新增开发自己组件需求的两个必备接口
// BeanPostProcessor是在Bean对象实例化之后修改Bean对象，也可以替换Bean对象，这部分与后面要实现的AOP功能有密切联系
// Spring源码的描述：提供了修改新实例化Bean对象的扩展点
// BeanFactoryPostProcess、BeanPostProcessor这两个接口非常重要，如果以后要做一些关于 Spring 中间件的开发时，
// 如果需要用到 Bean 对象的获取以及修改一些属性信息，那么就可以使用这两个接口了。
// 同时 BeanPostProcessor 也是实现 AOP 切面技术的关键所在
public interface BeanPostProcessor {

    /**
     * 在 Bean 对象执行初始化方法之前，执行此方法
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在 Bean 对象执行初始化方法之后，执行此方法
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
}
