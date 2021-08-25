package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;

import java.util.Map;

/**
 * Description:
 * date: 2021/8/10 14:22
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface ListableBeanFactory extends BeanFactory {

    /*
      ListableBeanFactory实现了对Bean实例的枚举，本项目提供了按照Bean的类型来获取Bean的实例
     */

    /**
     * 按照类型返回 Bean 实例
     *
     * @param type
     * @param <T>
     * @return
     * @throws BeansException
     */
    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

    /**
     * Return the names of all beans defined in this registry.
     * <p>
     * 返回注册表中所有的Bean名称
     */
    String[] getBeanDefinitionNames();
}
