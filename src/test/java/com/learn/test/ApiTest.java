package com.learn.test;

import com.learn.myspring.day1.BeanDefinition;
import com.learn.myspring.day1.BeanFactory;
import com.learn.myspring.day2.DefaultListableBeanFactory;
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
        BeanFactory beanFactory = new BeanFactory();

        //注册bean
        BeanDefinition beanDefinition = new BeanDefinition(new UserServiceImpl());
        beanFactory.registerBeanDefinition("userServiceImpl", beanDefinition);

        //获取bean
        UserServiceImpl userServiceImpl = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl.queryUserInfo();
    }

    @Test
    public void test_test_BeanFactory2() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 2.注册 bean
        com.learn.myspring.day2.BeanDefinition beanDefinition = new com.learn.myspring.day2.BeanDefinition(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userServiceImpl", beanDefinition);
        // 3.第一次获取 bean
        UserServiceImpl userServiceImpl = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl.queryUserInfo();
        // 4.第二次获取 bean from Singleton,第二次获取Bean的时候，在getSingleton中已经可以拿到缓存的Bean了
        UserServiceImpl userServiceImpl_singleton = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl_singleton.queryUserInfo();
    }

}
