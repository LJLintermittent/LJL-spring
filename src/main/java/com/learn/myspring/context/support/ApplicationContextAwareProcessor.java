package com.learn.myspring.context.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.config.BeanPostProcessor;
import com.learn.myspring.context.ApplicationContext;
import com.learn.myspring.context.ApplicationContextAware;

/**
 * Description:
 * date: 2021/8/15 14:40
 * Package: com.learn.myspring.context.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    /**
     * 由于 ApplicationContext 的获取并不能直接在创建 Bean 时候就可以拿到，
     * 所以需要在 refresh 操作时，把 ApplicationContext 写入到一个包装的 BeanPostProcessor 中去，
     * 再由 AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization 方法调用
     */
    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
