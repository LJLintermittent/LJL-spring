package com.learn.test.bean;

import com.learn.myspring.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/16 15:13
 * Package: com.learn.test.bean
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
/*
  类似与Mybatis框架中的代理操作
 */
public class ProxyBeanFactory implements FactoryBean<IUserDao> {

    @Override
    public IUserDao getObject() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> {
            Map<String, String> map = new HashMap<>(16);
            map.put("1", "李佳乐");
            map.put("2", "断续");
            map.put("3", "LJL");
            return "代理类调用了代理方法：" + method.getName() + "(),输出结果：" + map.get(args[0].toString());
        };
        return (IUserDao) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class[]{IUserDao.class}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return IUserDao.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
