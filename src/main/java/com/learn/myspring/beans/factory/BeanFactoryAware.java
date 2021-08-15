package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;

/**
 * Description:
 * date: 2021/8/15 14:20
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// Interface to be implemented by beans that wish to be aware of their owning {@link BeanFactory}.
// 实现此接口，就能感知到所属的 BeanFactory
public interface BeanFactoryAware extends Aware{

    void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
