package com.learn.test.common;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.config.BeanPostProcessor;
import com.learn.test.bean.UserService;

/**
 * Description:
 * date: 2021/8/10 16:07
 * Package: com.learn.test.common
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("userService".equals(beanName)) {
            UserService userService = (UserService) bean;
            userService.setLocation("改为：北京");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
