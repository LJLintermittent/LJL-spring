package com.learn.myspring.beans.factory;

/**
 * Description:
 * date: 2021/8/15 14:32
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// Callback that allows a bean to be aware of the bean{@link ClassLoader class loader};
// that is, the class loader used by the present bean factory to load bean classes.
// 实现此接口，既能感知到所属的 ClassLoader
public interface BeanClassLoaderAware extends Aware{

    void setBeanClassLoader(ClassLoader classLoader);

}

