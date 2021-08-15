package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.UserServiceForAware;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/15 15:18
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class AwareTest {

    @Test
    public void test_xml_aware() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring1.xml");
        applicationContext.registerShutdownHook();
        UserServiceForAware userService = applicationContext.getBean("userService", UserServiceForAware.class);
        String result = userService.queryUserInfo();
        System.out.println("测试：" + result);
        // 新增加的感知接口对应的具体实现(BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware)，
        // 已经可以如期输出结果
        System.out.println("ApplicationContextAware：" + userService.getApplicationContext());
        System.out.println("BeanFactoryAware：" + userService.getBeanFactory());
    }

}
