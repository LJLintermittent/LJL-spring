package com.learn.myspring.day2;

/**
 * Description:
 * date: 2021/8/5 19:05
 * Package: com.learn.myspring.day2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    /**
     * 在 AbstractAutowireCapableBeanFactory 类中实现了 Bean 的实例化操作 newInstance
     * 在处理完 Bean 对象的实例化后，直接调用 addSingleton 方法存放到单例对象的缓存中去。
     */
    @Override
    protected Object createBean(String name, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            bean = beanDefinition.getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        addSingleton(name, bean);
        return bean;
    }
}
