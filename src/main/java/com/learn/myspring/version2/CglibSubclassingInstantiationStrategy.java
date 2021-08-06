package com.learn.myspring.version2;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Constructor;

/**
 * Description:
 * date: 2021/8/6 22:12
 * Package: com.learn.myspring.version2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */

// Cglib 实例化
@SuppressWarnings("all")
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {

    // Cglib创建有构造函数的 Bean 也非常方便,在这里我更加简化的处理了，
    // 阅读Spring源码还会看到 CallbackFilter 等实现
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args) throws BeansException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        if (constructor == null) {
            return enhancer.create();
        }
        return enhancer.create(constructor.getParameterTypes(), args);
    }
}
