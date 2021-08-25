package com.learn.myspring.beans.factory.config;

import com.learn.myspring.beans.factory.HierarchicalBeanFactory;
import com.learn.myspring.utils.StringValueResolver;

/**
 * Description:
 * date: 2021/8/10 14:26
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /*
      ConfigurableBeanFactory在spring中提供了设置父容器接口，指定类加载器的职能，
      并且为当前容器工厂设计Bean的定制型的解析处理器，类型处理器，主要目的是实现对BeanFactory的可配置性
     */

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 销毁单例对象
     */
    void destroySingletons();

    /**
     * Add a String resolver for embedded values such as annotation attributes.
     *
     * @param valueResolver the String resolver to apply to embedded values
     * @since 3.0
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * Resolve the given embedded value, e.g. an annotation attribute.
     *
     * @param value the value to resolve
     * @return the resolved value (may be the original value as-is)
     * @since 3.0
     */
    String resolveEmbeddedValue(String value);
}
