package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.UserServiceForFactory;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

/**
 * Description:
 * date: 2021/8/16 15:24
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class BeanFactoryTest {

    /*
       测试单例Bean的获取和原型模式Bean的获取
     */
    @Test
    public void test_prototype() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring4.xml");
        applicationContext.registerShutdownHook();
        UserServiceForFactory userService1 = applicationContext.getBean("userService", UserServiceForFactory.class);
        UserServiceForFactory userService2 = applicationContext.getBean("userService", UserServiceForFactory.class);
        System.out.println("对象1：" + userService1);
        System.out.println("对象2：" + userService2);

        System.out.println(userService1 + "对象的十六进制哈希：" + Integer.toHexString(userService1.hashCode()));
        System.out.println(ClassLayout.parseInstance(userService1).toPrintable());
    }

    @Test
    public void test_factory_bean(){
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring4.xml");
        applicationContext.registerShutdownHook();
        UserServiceForFactory userService = applicationContext.getBean("userService", UserServiceForFactory.class);
        System.out.println("测试结果：" + userService.queryUserInfo());

    }
}
