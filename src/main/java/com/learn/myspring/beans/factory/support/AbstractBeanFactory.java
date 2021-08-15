package com.learn.myspring.beans.factory.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.BeanFactory;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.beans.factory.config.BeanPostProcessor;
import com.learn.myspring.beans.factory.config.ConfigurableBeanFactory;
import com.learn.myspring.utils.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * date: 2021/8/5 18:50
 * Package: com.learn.myspring.beans.factory.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /**
     * 抽象类定义模板方法
     * AbstractBeanFactory 首先继承了 DefaultSingletonBeanRegistry，也就具备了使用单例注册类方法。
     * 接下来很重要的一点是关于接口 BeanFactory 的实现，在方法 getBean 的实现过程中可以看到，
     * 主要是对单例 Bean 对象的获取以及在获取不到时需要拿到 Bean 的定义做相应 Bean 实例化操作。
     * 那么 getBean 并没有自身的去实现这些方法，而是只定义了调用过程以及提供了抽象方法，由实现此抽象类的其他类做相应实现
     */

    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return doGetBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean(name);
    }

    protected <T> T doGetBean(final String name, final Object[] args) {
        Object bean = getSingleton(name);
        if (bean != null) {
            return (T) bean;
        }
        BeanDefinition beanDefinition = getBeanDefinition(name);
        return (T) createBean(name, beanDefinition, args);
    }

    /**
     * 后续继承抽象类 AbstractBeanFactory 的类有两个
     * 包括：AbstractAutowireCapableBeanFactory、DefaultListableBeanFactory，
     * 这两个类分别做了相应的实现处理
     */
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
}
