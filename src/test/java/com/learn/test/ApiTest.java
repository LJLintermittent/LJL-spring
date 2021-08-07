package com.learn.test;

import com.learn.myspring.core.*;
import org.junit.Test;

/**
 * Description:
 * date: 2021/7/25 23:14
 * Package: com.learn.test
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ApiTest {

    @Test
    public void test_test_BeanFactory1() {
        //初始化BeanFactory
//        BeanFactory beanFactory = new BeanFactory();

        //注册bean
//        BeanDefinition beanDefinition = new BeanDefinition(new UserServiceImpl());
//        beanFactory.registerBeanDefinition("userServiceImpl", beanDefinition);

        //获取bean
//        UserServiceImpl userServiceImpl = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
//        userServiceImpl.queryUserInfo();
    }

    @Test
    public void test_test_BeanFactory2() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 2.注册 bean
        com.learn.myspring.core.BeanDefinition beanDefinition = new com.learn.myspring.core.BeanDefinition(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userServiceImpl", beanDefinition);
        // 3.第一次获取 bean
        UserServiceImpl userServiceImpl = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl.queryUserInfo();
        // 4.第二次获取 bean from Singleton,第二次获取Bean的时候，在getSingleton中已经可以拿到缓存的Bean了
        UserServiceImpl userServiceImpl_singleton = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl_singleton.queryUserInfo();
    }

    @Test
    public void test_test_BeanFactory3() {
        // 初始化bean工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 定义bean
        BeanDefinition beanDefinition = new BeanDefinition(UserServiceImpl.class);
        // 注册定好了的bean
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        // 获取bean
        UserServiceImpl userService = (UserServiceImpl) beanFactory.getBean("userService", "李佳乐");
        userService.queryUserInfo();
    }

    @Test
    public void test_BeanFactory4() {
        // 初始化beanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 注册UserDao
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));
        // UserService 设置属性[uId、userDao]
        PropertyValues propertyValues = new PropertyValues();
        //普通属性填充
        propertyValues.addPropertyValue(new PropertyValue("uid", "1"));
        //对象属性填充
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));
        // UserService 获取bean
        BeanDefinition beanDefinition = new BeanDefinition(UserServiceImpl.class, propertyValues);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        UserServiceImpl userService = (UserServiceImpl) beanFactory.getBean("userService");
        userService.queryUserInfo();

    }

}
