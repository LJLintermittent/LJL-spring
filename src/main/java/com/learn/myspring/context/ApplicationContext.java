package com.learn.myspring.context;

import com.learn.myspring.beans.factory.HierarchicalBeanFactory;
import com.learn.myspring.beans.factory.ListableBeanFactory;
import com.learn.myspring.core.io.ResourceLoader;

/**
 * Description:
 * date: 2021/8/10 14:14
 * Package: com.learn.myspring.context
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 这个接口的顶级接口是BeanFactory，也就是说他拥有BeanFactory的方法，比如各种重载的getBean()
// 这个接口满足于自动识别，资源加载，容器事件，监听器等功能，同时例如一些国际化支持，单例Bean自动初始化等
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {


}
