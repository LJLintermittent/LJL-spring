package com.learn.myspring.beans.factory.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.DisposableBean;
import com.learn.myspring.beans.factory.ObjectFactory;
import com.learn.myspring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * date: 2021/8/5 18:44
 * Package: com.learn.myspring.beans.factory.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// DefaultSingletonBeanRegistry提供单例对象的注册操作，
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * 在 DefaultSingletonBeanRegistry 中主要实现 getSingleton 方法，
     * 同时实现了一个受保护的 addSingleton 方法，这个方法可以被继承此类的其他类调用。
     * 包括：AbstractBeanFactory 以及继承的 DefaultListableBeanFactory 调用。
     */

    /**
     * Internal marker for a null singleton object:
     * used as marker value for concurrent Maps (which don't support null values).
     */
    /**
     * Internal marker for a null singleton object:
     * used as marker value for concurrent Maps (which don't support null values).
     */
    protected static final Object NULL_OBJECT = new Object();

    /**
     * 一级缓存，存放普通单例对象
     */
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 二级缓存，提前暴露对象，没有完全实例化的对象，半成品单例对象
     */
    protected final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();

    /**
     * 三级缓存，存放代理对象，单例工厂对象
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();

    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = earlySingletonObjects.get(beanName);
            //判断二级缓存中是否有对象，这个对象就是代理对象，因为只有代理对象才会被放到三级缓存中
            if (singletonObject == null) {
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!this.singletonObjects.containsKey(beanName)) {
            this.singletonFactories.put(beanName, singletonFactory);
            this.earlySingletonObjects.remove(beanName);
        }
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    public void destroySingletons() {
        Set<String> keySet = this.disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();

        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }


}
