package com.learn.test.unittest;

import com.learn.myspring.aop.AdvisedSupport;
import com.learn.myspring.aop.TargetSource;
import com.learn.myspring.aop.aspectj.AspectJExpressionPointcut;
import com.learn.myspring.aop.framework.Cglib2AopProxy;
import com.learn.myspring.aop.framework.JdkDynamicAopProxy;
import com.learn.test.bean.aopbean.IUserService;
import com.learn.test.bean.aopbean.UserServiceInterceptor;
import org.junit.Test;

/**
 * Description:
 * date: 2021/8/18 17:15
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class AOPTest {

    @Test
    public void test_dynamic() {
        com.learn.test.bean.aopbean.UserService userService = new com.learn.test.bean.aopbean.UserService();
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(userService));
        advisedSupport.setMethodInterceptor(new UserServiceInterceptor());
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* com.learn.test.bean.aopbean.IUserService.*(..))"));
        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        System.out.println("jdk动态代理：" + proxy_jdk.queryUserInfo());

        System.out.println("-------------------分割符--------------------");

        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
        System.out.println("cglib动态代理：" + proxy_cglib.register("李佳乐"));

    }
}
