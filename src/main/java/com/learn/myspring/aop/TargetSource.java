package com.learn.myspring.aop;

import com.learn.myspring.utils.ClassUtils;

/**
 * Description:
 * date: 2021/8/18 17:39
 * Package: com.learn.myspring.aop
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class TargetSource {

    /*
      spring代理与aop：spring代理在支持aop，面向切面编程的同时，为spring自身的扩展及对其他框架的融合奠定了非常好的基础
      spring代理分为jdk动态代理和cglib动态代理：
      jdk动态代理是使用java自带的反射技术生成一个实现代理接口的匿名类，在执行具体方法之前调用invokehandler进行处理
      cglib动态代理是使用ASM开源包，将代理对象类的class文件加载进来，然后利用字节码技术修改class文件的字节码生成子类，进而实现代理类
      在实现spring的aop之前，我先谈论一下oop：
      oop是一种面向对象的程序设计，对象在显示支持面向对象的语言中，一般指类在内存中装载的实例，具有相关的成员变量和成员函数，或者叫方法
      我们通过抽象的方式将对象的共同特征总结出来构造类，（共同模型）主要关心对象包含哪些属性及行为，但是不关心具体的细节，从而达到软件工程的
      的要求：重用性，灵活性和扩展性
      aop可以说是oop的补充和完善，oop通过引入封装，继承和多态性等概念来建立一种对象的层次结构，用于模拟公共的一个行为集合，但在需要为分散的
      对象引入公共的行为时就无能为力了，比如日志功能，因为日志代码往往水平分散地散步在所有对象的层次中，却与它所在对象的核心功能是毫无关联的。
      以及其他类型如安全性，异常处理等非业务代码，也就是说oop允许我们定义从上到下的关系，但并不适合从左到右的关系，这种散步在各处的毫无关系的代码
      被称为横切代码，在oop的设计中有大量的重复代码，不利于各个模块的重用
      在以下场景中，适合使用aop：
      1.组件代码与业务代码耦合，例如日志功能，事务功能，异常处理，统一拦截，数据提取等，很多开源组件都是利用AOP的切面编程特性实现零入侵的
      2.代码高度重用，功能可配置。
     */

    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * Return the type of targets returned by this {@link TargetSource}.
     * <p>Can return <code>null</code>, although certain usages of a
     * <code>TargetSource</code> might just work with a predetermined
     * target class.
     *
     * @return the type of targets returned by this {@link TargetSource}
     */
    public Class<?>[] getTargetClass() {
        Class<?> clazz = this.target.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        return clazz.getInterfaces();
    }

    /**
     * Return a target instance. Invoked immediately before the
     * AOP framework calls the "target" of an AOP method invocation.
     *
     * @return the target object, which contains the joinpoint
     * @throws Exception if the target object can't be resolved
     */
    public Object getTarget() {
        return this.target;
    }

}
