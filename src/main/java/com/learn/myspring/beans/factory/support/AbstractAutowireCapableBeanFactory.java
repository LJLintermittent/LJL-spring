package com.learn.myspring.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.PropertyValue;
import com.learn.myspring.beans.PropertyValues;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.beans.factory.config.BeanReference;

import java.lang.reflect.Constructor;

/**
 * Description:
 * date: 2021/8/5 19:05
 * Package: com.learn.myspring.core
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {

    //首先在 AbstractAutowireCapableBeanFactory 抽象类中定义了一个创建对象的实例化策略属性类
    // InstantiationStrategy instantiationStrategy，这里选择Cglib的实现类
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    /**
     * 在 AbstractAutowireCapableBeanFactory 类中实现了 Bean 的实例化操作 newInstance
     * 在处理完 Bean 对象的实例化后，直接调用 addSingleton 方法存放到单例对象的缓存中去。
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            applyPropertyValues(beanName,bean,beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }
        addSingleton(beanName, bean);
        return bean;
    }

    /**
     * 创建Bean实例，调用cglib的实现类
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        // 有构造函数的类需要实例化时，则需要使用 getDeclaredConstructor 获取构造函数，之后在通过传递参数进行实例化
        // 此行代码最为核心
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        // 将获得的所有构造器遍历，找到参数列表长度相同的构造器，就是我们需要拿这个构造函数来进行实例化
        for (Constructor<?> ctor : declaredConstructors) {
            if (args != null && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);

    }

    /**
     * Bean属性填充
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                if (value instanceof BeanReference) {
                    //A依赖B,获取B的实例化
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values：" + beanName);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

}
