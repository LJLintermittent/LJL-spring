package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.autowired.IUserService;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/21 23:06
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class autowiredTest {

    @Test
    public void test_autowired() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring7.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());

    }
}
