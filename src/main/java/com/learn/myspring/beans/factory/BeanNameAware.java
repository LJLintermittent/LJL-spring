package com.learn.myspring.beans.factory;

/**
 * Description:
 * date: 2021/8/15 14:37
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
//Interface to be implemented by beans that want to be aware of their bean name in a bean factory.
//实现此接口，既能感知到所属的 BeanName
public interface BeanNameAware extends Aware{

    void setBeanName(String name);

}
