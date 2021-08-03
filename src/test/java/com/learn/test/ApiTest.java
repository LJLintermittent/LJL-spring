package com.learn.test;

import com.learn.myspring.day1.BeanDefinition;
import com.learn.myspring.day1.BeanFactory;
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
    public void test_test_BeanFactory(){
        //初始化BeanFactory
        BeanFactory beanFactory = new BeanFactory();

        //注册bean
        BeanDefinition beanDefinition = new BeanDefinition(new UserServiceImpl());
        beanFactory.registerBeanDefinition("userServiceImpl",beanDefinition);

        //获取bean
        UserServiceImpl userServiceImpl = (UserServiceImpl) beanFactory.getBean("userServiceImpl");
        userServiceImpl.queryUserInfo();
    }

}
