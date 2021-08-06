package com.learn.myspring.version1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * date: 2021/7/25 23:11
 * Package: com.learn.myspring.day1
 * <p>
 * 凡是可以存放数据的具体数据结构实现，都可以称之为容器。例如：ArrayList、LinkedList、HashSet等
 * 但在 Spring Bean 容器的场景下，我们需要一种可以用于存放和名称索引式的数据结构，所以选择 HashMap 是最合适不过的。
 * 一个Sping bean容器的实现，大概需要Bean的定义，注册，获取三个步骤
 * 注册就是把定义了的Bean放到HashMap中。
 * 获取就是拿key，也就是拿Bean的名字来获取
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// BeanFactory,代表了Bean对象工厂，可以存放Bean定义到Map中以及获取
@SuppressWarnings("all")
public class BeanFactory {
    //类名称使用跟源码一样，进行简化版实现

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // Map的get方法，获取Bean
    public Object getBean(String name) {
        return beanDefinitionMap.get(name).getBean();
    }

    // Map的put方法，添加定义了的Bean
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }

}
