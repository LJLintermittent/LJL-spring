package com.learn.myspring.beans.factory;


/**
 * Description:
 * date: 2021/8/10 14:22
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface HierarchicalBeanFactory extends BeanFactory {
    /*
      在spring中HierarchicalBeanFactory只是对BeanFactory进行了扩展，定义了父容器以及按断
      当前Bean的名称是否在当前Bean工厂中
     */

}
