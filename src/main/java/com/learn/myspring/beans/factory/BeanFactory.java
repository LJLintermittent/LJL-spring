package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;

/**
 * Description:
 * date: 2021/8/5 18:51
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface BeanFactory {

    /*
      BeanFactory接口是Bean容器设计中基本的职责定义接口，定义了按照名称，参数
      以及requiredType，也就是类型这几个维度来获取Bean的职能
      spring的核心功能是对Bean的定义注册和依赖，而spring容器提供了依赖注入这个特征，以实现spring容器对Bean的管理
      而且使用IOC实现了对Bean的配置与业务代码的隔离
     */

    Object getBean(String name) throws BeansException;

    // 解决实例化带有构造器的Bean对象时的入参问题
    // 重载，方法名相同的方法 可以有不同的参数列表，方法返回值可以相同，也可以不同
    // 方法的重载和重写都是实现多态的方式，区别在于前者实现的是编译时的多态性，
    // 而后者实现的是运行时的多态性。重载发生在一个类中，
    // 同名的方法如果有不同的参数列表（参数类型不同、参数个数不同或者二者都不同）则视为重载；
    // 重写发生在子类与父类之间，重写要求子类被重写方法与父类被重写方法有相同的参数列表，
    // 有兼容的返回类型，比父类被重写方法更好访问，不能比父类被重写方法声明更多的异常（里氏代换原则）。
    // 重载对返回类型没有特殊的要求，不能根据返回类型进行区分。

    // BeanFactory 中我们重载了一含有入参信个息 args 的 getBean 方法，这样就可以方便的传递入参给构造函数实例化了。
    Object getBean(String name, Object... args) throws BeansException;

    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    <T> T getBean(Class<T> requiredType) throws BeansException;
}
