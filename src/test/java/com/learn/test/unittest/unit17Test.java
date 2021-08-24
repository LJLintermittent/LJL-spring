package com.learn.test.unittest;

import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.circledepend.Husband;
import com.learn.test.bean.circledepend.Wife;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/24 19:00
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class unit17Test {

    @Test
    public void test_circular() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring9.xml");
        Husband husband = applicationContext.getBean("husband", Husband.class);
        Wife wife = applicationContext.getBean("wife", Wife.class);
        System.out.println("老公的媳妇：" + husband.queryWife());
        System.out.println("媳妇的老公：" + wife.queryHusband());
    }

}
