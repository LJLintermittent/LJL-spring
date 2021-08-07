package com.learn.myspring.core;

/**
 * Description:
 * date: 2021/8/5 18:41
 * Package: com.learn.myspring.core
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class BeanDefinition {

    /**
     * 在Bean的定义中，把Object Bean替换为了Class，这样就可以把Bean的实例化操作放到容器中处理
     */
    private Class beanClass;

    /**
     * Bean的属性集合，用于将Bean实例化完成以后进行属性填充，如果有的话
     */
    private PropertyValues propertyValues;


    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
        this.propertyValues = new PropertyValues();
    }

    /**
     * 在 Bean 注册的过程中是需要传递 Bean 的信息
     */
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
}
