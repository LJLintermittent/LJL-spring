package com.learn.test.unittest;

import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;
import com.learn.myspring.beans.factory.xml.XmlBeanDefinitionReader;
import com.learn.myspring.context.support.ClassPathXmlApplicationContext;
import com.learn.test.bean.UserService;
import com.learn.test.common.MyBeanFactoryPostProcessor;
import com.learn.test.common.MyBeanPostProcessor;
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

    /**
     * MyBeanFactoryPostProcessor 和 MyBeanPostProcessor 的处理:
     * 一个是在BeanDefinition 加载完成 & Bean实例化之前，修改 BeanDefinition 的属性值，
     * 另外一个是在Bean实例化之后，修改 Bean 属性信息
     */
    //不使用应用上下文，使用了BeanPostProcessor，BeanFactoryPostProcessor
    @Test
    public void test_xml1() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");
        MyBeanFactoryPostProcessor myBeanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
        myBeanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        MyBeanPostProcessor myBeanPostProcessor = new MyBeanPostProcessor();
        beanFactory.addBeanPostProcessor(myBeanPostProcessor);
        UserService userService = beanFactory.getBean("userService", UserService.class);
        String s = userService.queryUserInfo();
        System.out.println("测试结果：" + s);
    }

    //使用了应用上下文，使用了BeanPostProcessor，BeanFactoryPostProcessor
    @Test
    public void test_xml2() {
        //跟上面的区别是直接将我们自定义实现spring接口的类也通过配置文件的方式
        //用bean标签配置到了文件中，可以直接读取到
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:springPostProcessor.xml");
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

    /**
     * 测试初始化方法和销毁方法：
     * 此次测试主要完成了关于初始和销毁在使用接口定义 implements InitializingBean, DisposableBean
     * 和在spring.xml中配置 init-method="initDataMethod" destroy-method="destroyDataMethod"
     * 的两种具体在 AbstractAutowireCapableBeanFactory 完成初始方法和 AbstractApplicationContext
     * 处理销毁动作的具体实现过程
     * <p>
     * 目前为止，此框架既可以在Bean注册完成实例化之前进行BeanFactoryPostProcessor 操作，
     * 也可以在实例化过程中执行前置操作和后置操作，现在又可以执行Bean的初始化方法和销毁方法
     */
    @Test
    public void test_xml3() {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath:spring1.xml");
        applicationContext.registerShutdownHook();
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

    @Test
    public void test_hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close！")));
    }

    @Test
    public void testIsAssignableFrom() {
        // A可以由B转换而来，描述的是继承关系，或者接口与接口实现类的关系
        // 超类可以由任意类转换而来，或者可以理解为 判断A是B的父类
        System.out.println(Object.class.isAssignableFrom(UserService.class)); //true
        // B实现了A这个接口
        System.out.println(ITest.class.isAssignableFrom(ITestImpl.class)); //true
    }
}
