package com.learn.test.unittest;

import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;
import com.learn.myspring.beans.factory.xml.XmlBeanDefinitionReader;
import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.UserService;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/10 0:57
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class XMLTest {

    @Test
    public void test_xml() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");
        UserService userService = beanFactory.getBean("userService", UserService.class);
        String s = userService.queryUserInfo();
        System.out.println("测试结果：" + s);
    }

    @Test
    public void test_xml2() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:springPostProcessor.xml");
        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }
}
