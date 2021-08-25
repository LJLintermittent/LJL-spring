package com.learn.myspring.beans.factory.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;
import com.learn.myspring.beans.factory.config.BeanDefinition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * date: 2021/8/5 19:11
 * Package: com.learn.myspring.beans.factory.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    /*
      DefaultListableBeanFactory提供了对Bean容器的完全成熟的默认实现，可以直接对外使用
     */

    /**
     * DefaultListableBeanFactory 继承了 AbstractAutowireCapableBeanFactory 类，
     * 也就具备了接口 BeanFactory 和 AbstractBeanFactory 等一连串的功能实现。
     * 所以有时候会看到一些类的强转，调用某些方法，也是因为强转的类实现接口或继承了某些类。
     * <p>
     * 除此之外这个类还实现了接口 BeanDefinitionRegistry
     * 中的 registerBeanDefinition(String beanName, BeanDefinition beanDefinition) 方法，
     * 当然还有一个 getBeanDefinition 的实现，这个方法是抽象类 AbstractBeanFactory 中定义的抽象方法。
     * 现在注册Bean定义与获取Bean定义就可以同时使用了，接口定义了注册，抽象类定义了获取，
     * 都集中在 DefaultListableBeanFactory 中的 beanDefinitionMap 里
     */

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        beanDefinitionMap.keySet().forEach(this::getBean);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        if (1 == beanNames.size()) {
            return getBean(beanNames.get(0), requiredType);
        }

        throw new BeansException(requiredType + "expected single bean but found " + beanNames.size() + ": " + beanNames);
    }
}
