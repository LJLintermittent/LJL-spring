package com.learn.myspring.version2;

import java.lang.reflect.Constructor;

/**
 * Description:
 * date: 2021/8/6 21:52
 * Package: com.learn.myspring.day2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */

//定义实例化策略接口
@SuppressWarnings("all")
public interface InstantiationStrategy {

    /**
     * @param beanDefinition
     * @param beanName
     * @param constructor:里面包含了一些必要的类信息，有这个参数的目的就是为了拿到符合入参信息相对应的构造函数
     * @param args:具体的入参信息，最终实例化的时候会用到
     * @return
     * @throws BeansException
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) throws BeansException;


}
