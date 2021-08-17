package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.event.CustomEvent;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/17 16:40
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class EventTest {

    @Test
    public void test_event() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring5.xml");
        applicationContext.publishEvent(new CustomEvent(applicationContext, 1L, "李佳乐加入了阿里巴巴"));
        applicationContext.registerShutdownHook();

    }
}
