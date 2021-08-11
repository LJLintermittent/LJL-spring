package com.learn.myspring.context.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;
import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;

/**
 * Description:
 * date: 2021/8/10 14:17
 * Package: com.learn.myspring.context.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 获取Bean工厂和加载资源
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    private DefaultListableBeanFactory beanFactory;

    /**
     * 在 refreshBeanFactory()中主要是获取了DefaultListableBeanFactory的实例化以及对资源配置的加载操作
     * loadBeanDefinitions(beanFactory)
     * 在加载完成后即可完成对 spring.xml配置文件中Bean对象的定义和注册，
     * 同时也包括实现了接口 BeanFactoryPostProcessor、BeanPostProcessor的配置Bean信息
     */
    @Override
    protected void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    //只是定义了抽象方法，继续由其他抽象类实现
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
