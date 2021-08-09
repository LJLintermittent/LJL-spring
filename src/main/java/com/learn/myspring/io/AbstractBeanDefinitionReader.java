package com.learn.myspring.io;

import com.learn.myspring.core.BeanDefinitionRegistry;

/**
 * Description:
 * date: 2021/8/9 23:25
 * Package: com.learn.myspring.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//抽象类实现了BeanDefinitionReader的前两个方法，并提供了构造函数，让外部的调用使用方，把Bean定义注入类，传递进来
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    private ResourceLoader resourceLoader;

    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.beanDefinitionRegistry = registry;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return beanDefinitionRegistry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
