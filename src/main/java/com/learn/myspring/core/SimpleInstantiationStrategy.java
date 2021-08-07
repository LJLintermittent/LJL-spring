package com.learn.myspring.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Description:
 * date: 2021/8/6 21:56
 * Package: com.learn.myspring.core
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */

//JDK实例化
@SuppressWarnings("all")
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    /**
     * 1.首先通过 beanDefinition 获取 Class 信息，这个 Class 信息是在 Bean 定义的时候传递进去的。
     * 2.然后判断Constructor，如果为空，则为无构造函数的实例化，否则就是有构造函数的实例化
     * 3.这里我们重点关注有构造函数的实例化，
     * 实例化方式为 clazz.getDeclaredConstructor(ctor.getParameterTypes()).newInstance(args);，
     * 把入参信息传递给 newInstance 进行实例化。
     *
     * @param beanDefinition
     * @param beanName
     * @param constructor:里面包含了一些必要的类信息，有这个参数的目的就是为了拿到符合入参信息相对应的构造函数
     * @param args:具体的入参信息，最终实例化的时候会用到
     * @return
     * @throws BeansException
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) throws BeansException {
        Class clazz = beanDefinition.getBeanClass();
        try {
            if (constructor != null) {
                return clazz.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(args);
            } else {
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BeansException("Failed to instantiate [" + clazz.getName() + "]", e);
        }
    }
}
