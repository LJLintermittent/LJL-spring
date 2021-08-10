package com.learn.test.unittest;

import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;
import com.learn.myspring.beans.factory.xml.XmlBeanDefinitionReader;
import com.learn.test.bean.UserServiceImpl;
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
        XmlBeanDefinitionReader reader  = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");
        UserServiceImpl userService = (UserServiceImpl) beanFactory.getBean("serServiceImpl", UserServiceImpl.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);

    }
}
