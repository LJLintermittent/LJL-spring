package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.testcircle.A;
import com.learn.test.bean.testcircle.B;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/25 14:49
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class dubugcircle {

    // 如果使用原型Bean测试循环依赖，直接StackOverflow
    @Test
    public void testcricle() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring10.xml");
        A a = applicationContext.getBean("a", A.class);
        B b = applicationContext.getBean("b", B.class);

        System.out.println(a);
        System.out.println(b);
    }
}

