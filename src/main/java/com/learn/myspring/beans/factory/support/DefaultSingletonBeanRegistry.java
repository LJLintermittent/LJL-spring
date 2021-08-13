package com.learn.myspring.beans.factory.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.DisposableBean;
import com.learn.myspring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * date: 2021/8/5 18:44
 * Package: com.learn.myspring.beans.factory.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * 在 DefaultSingletonBeanRegistry 中主要实现 getSingleton 方法，
     * 同时实现了一个受保护的 addSingleton 方法，这个方法可以被继承此类的其他类调用。
     * 包括：AbstractBeanFactory 以及继承的 DefaultListableBeanFactory 调用。
     */

    private Map<String, Object> singletonObjects = new HashMap<>();

    private Map<String, DisposableBean> disposableBeanMap = new HashMap<>();

    @Override
    public Object getSingleton(String name) {
        return singletonObjects.get(name);
    }

    // 这个方法可以被继承此类的其他类调用。
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeanMap.put(beanName, bean);
    }

    public void destroySingletons() {
        Set<String> keySet = this.disposableBeanMap.keySet();
        Object[] disposableBeanNames = keySet.toArray();
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeanMap.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '"
                        + beanName + "' threw an exception", e);
            }
        }
    }


}
