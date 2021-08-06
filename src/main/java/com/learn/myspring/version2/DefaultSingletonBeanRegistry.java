package com.learn.myspring.version2;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/5 18:44
 * Package: com.learn.myspring.day2
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

    @Override
    public Object getSingleton(String name) {
        return singletonObjects.get(name);
    }

    // 这个方法可以被继承此类的其他类调用。
    protected void addSingleton(String name, Object singletonObject) {
        singletonObjects.put(name, singletonObject);
    }
}
