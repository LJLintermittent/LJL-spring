package com.learn.test.bean.circledepend;

import com.learn.myspring.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Description:
 * date: 2021/8/24 18:49
 * Package: com.learn.test.bean.circledepend
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class HusbandMother implements FactoryBean<IMother> {

    @Override
    public IMother getObject() throws Exception {
        return (IMother) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), new Class[]{IMother.class},
                (proxy, method, args) -> "婚后媳妇妈妈的职责被婆婆代理了！" + method.getName());
    }

    @Override
    public Class<?> getObjectType() {
        return IMother.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
