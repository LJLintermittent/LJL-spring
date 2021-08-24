package com.learn.test.bean.circledepend;

import com.learn.myspring.beans.factory.FactoryBean;

/**
 * Description:
 * date: 2021/8/24 18:49
 * Package: com.learn.test.bean.circledepend
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class HusbandMother implements FactoryBean<IMother> {

    @Override
    public IMother getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
