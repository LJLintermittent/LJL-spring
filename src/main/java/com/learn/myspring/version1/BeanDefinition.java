package com.learn.myspring.version1;

/**
 * Description:
 * date: 2021/7/25 23:11
 * Package: com.learn.myspring.day1
 * <p>
 * Spring包含并管理应用对象的配置和生命周期，
 * 在这个意义上它是一种用于承载对象的容器，你可以配置你的每个 Bean 对象是如何被创建的，
 * 这些 Bean 可以创建一个单独的实例或者每次需要时都生成一个新的实例，以及它们是如何相互关联构建和使用的。
 * 如果一个Bean对象交给 Spring 容器管理，那么这个 Bean 对象就应该以类似零件的方式被拆解后存放到 Bean的定义中，
 * 这样相当于一种把对象解耦的操作，可以由 Spring 更加容易的管理，就像处理循环依赖等操作。
 * 当一个 Bean 对象被定义存放以后，再由 Spring 统一进行装配，这个过程包括 Bean 的初始化、属性填充等，
 * 最终我们就可以完整的使用一个 Bean 实例化后的对象了。
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// BeanDefinition，用于定义Bean实例化信息，现在的实现是以一个Object存放对象
@SuppressWarnings("all")
public class BeanDefinition {

    //目前Bean的定义中，只有一个Object用于存放Bean对象
    private Object bean;

    public BeanDefinition(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }


}
