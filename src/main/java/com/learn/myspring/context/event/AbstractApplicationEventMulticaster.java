package com.learn.myspring.context.event;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.BeanFactory;
import com.learn.myspring.beans.factory.BeanFactoryAware;
import com.learn.myspring.context.ApplicationEvent;
import com.learn.myspring.context.ApplicationListener;
import com.learn.myspring.utils.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Description:
 * date: 2021/8/17 15:31
 * Package: com.learn.myspring.context.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
/*
   AbstractApplicationEventMulticaster是对事件广播器的公用方法提取，在这个类中可以实现一些基本功能，避免所有直接
   实现接口方还需要处理细节
   除了像 addApplicationListener、removeApplicationListener，这样的通用方法，
   这里这个类中主要是对 getApplicationListeners 和 supportsEvent 的处理
   getApplicationListeners()方法主要是摘取符合广播事件中的监听处理器，具体过滤动作在supportsEvent()方法中
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    private BeanFactory beanFactory;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Return a Collection of ApplicationListeners matching the given
     * event type. Non-matching listeners get excluded early.
     * @param event the event to be propagated. Allows for excluding
     * non-matching listeners early, based on cached matching information.
     * @return a Collection of ApplicationListeners
     * @see cn.bugstack.springframework.context.ApplicationListener
     */
    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
        LinkedList<ApplicationListener> allListeners = new LinkedList<ApplicationListener>();
        for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
            if (supportsEvent(listener, event)) allListeners.add(listener);
        }
        return allListeners;
    }

    /**
     * 监听器是否对该事件感兴趣
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
        Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();

        // 按照 CglibSubclassingInstantiationStrategy、SimpleInstantiationStrategy 不同的实例化类型，需要判断后获取目标 class
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;
        Type genericInterface = targetClass.getGenericInterfaces()[0];

        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();
        Class<?> eventClassName;
        try {
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("wrong event class name: " + className);
        }
        // 判定此 eventClassName 对象所表示的类或接口与指定的 event.getClass() 参数所表示的类或接口是否相同，或是否是其超类或超接口。
        // isAssignableFrom是用来判断子类和父类的关系的，或者接口的实现类和接口的关系的，默认所有的类的终极父类都是Object。如果A.isAssignableFrom(B)结果是true，证明B可以转换成为A,也就是A可以由B转换而来。
        return eventClassName.isAssignableFrom(event.getClass());
    }

}
