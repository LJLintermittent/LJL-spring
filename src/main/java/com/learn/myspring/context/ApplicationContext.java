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

    /*
      ApplicationContext是整个容器的基本功能定义接口，继承的顶级接口是BeanFactory，说明容器也是工厂的多态实现
      运用了代理的设计方式，它的实现类相当于持有一个BeanFactory实例，这个实例替它执行BeanFactory接口定义的功能
      虽然spring在ApplicationContext中也声明了BeanFactory接口中的功能,但是Beanfactory实例只是ApplicationContext中的一个属性
      由这个属性来帮助ApplicationContext对外提供beanfactory定义的功能实现
      ApplicationContext是围绕着spring的整体来设计的，从类型上看它虽然是Beanfactoy的实现类，但比beanfactory的功能更加强大，可以
      理解为ApplicationContext接口扩展了Beanfactory接口
      ApplicationContext是一个复杂的集成体，集成了环境接口，beanfactory接口，消息发布接口，配置源信息解析接口
     */
}
