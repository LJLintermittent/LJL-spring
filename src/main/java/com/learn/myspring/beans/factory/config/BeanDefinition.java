package com.learn.myspring.beans.factory.config;

import com.learn.myspring.beans.PropertyValues;

/**
 * Description:
 * date: 2021/8/5 18:41
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class BeanDefinition {

    /**
     * 用于将从spring.xml中解析到的bean对象作用范围填充到属性中
     */
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * 在Bean的定义中，把Object Bean替换为了Class，这样就可以把Bean的实例化操作放到容器中处理
     */
    private Class beanClass;

    /**
     * Bean的属性集合，用于将Bean实例化完成以后进行属性填充，如果有的话
     */
    private PropertyValues propertyValues;

    /**
     * 在 BeanDefinition 新增加了两个属性：initMethodName、destroyMethodName，
     * 这两个属性是为了在 spring.xml 配置的 Bean 对象中，
     * 可以配置 init-method="initDataMethod" destroy-method="destroyDataMethod" 操作，
     * 最终实现接口的效果是一样的。只不过一个是接口方法的直接调用，另外是一个在配置文件中读取到方法反射调用
     */
    private String initMethodName;

    private String destroyMethodName;

    private String scope = SCOPE_SINGLETON;

    private boolean singleton = true;

    private boolean prototype = false;

    public BeanDefinition(Class beanClass) {
        this(beanClass,null);
    }

    /**
     * 在 Bean 注册的过程中是需要传递 Bean 的信息
     */
    public BeanDefinition(Class beanClass, PropertyValues propertyValues) {
        this.beanClass = beanClass;
        this.propertyValues = propertyValues != null ? propertyValues : new PropertyValues();
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isPrototype() {
        return prototype;
    }

    public void setPrototype(boolean prototype) {
        this.prototype = prototype;
    }
}
