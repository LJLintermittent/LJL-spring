package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.annotation.IUserService;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/20 14:44
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class unit14Test {

    /*
       测试把占位符设置到配置文件中
     */
    @Test
    public void test_property() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring-property.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试占位符配置文件：" + userService);
    }

    /*
       测试包扫描
     */
    @Test
    public void test_scan(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-scan.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试包扫描：" + userService.queryUserInfo());

    }
}
