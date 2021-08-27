package com.learn.myspring.context.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;
import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;

/**
 * Description:
 * date: 2021/8/10 14:17
 * Package: com.learn.myspring.context.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 创建Bean工厂和加载资源
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    /*
      AbstractRefreshableApplicationContext是xmlwebapplicationcontext的核心父类
      在本项目中省去了xmlwebapplicationcontext
      仅用此接口来对AbstractApplicationContext的refreshBeanFactory做一个简单实现
      同时本身又是一个抽象类，定义了两个抽象方法loadBeanDefinitions交给其他类去实现，做一个职责划分
      并且提供了获取DefaultListableBeanFactory类的get方法
     */

    private DefaultListableBeanFactory beanFactory;

    /**
     * 在 refreshBeanFactory()中主要是获取了DefaultListableBeanFactory的实例化以及对资源配置的加载操作
     * loadBeanDefinitions(beanFactory)
     * 在加载完成后即可完成对 spring.xml配置文件中Bean对象的定义和注册，
     * 同时也包括实现了接口 BeanFactoryPostProcessor、BeanPostProcessor的配置Bean信息
     */
    @Override
    protected void refreshBeanFactory() throws BeansException {
        DefaultListableBeanFactory beanFactory = createBeanFactory();
        loadBeanDefinitions(beanFactory);
        this.beanFactory = beanFactory;
    }

    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    //只是定义了抽象方法，继续由其他抽象类实现
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
