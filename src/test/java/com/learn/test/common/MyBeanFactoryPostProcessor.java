package com.learn.test.common;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.PropertyValue;
import com.learn.myspring.beans.PropertyValues;
import com.learn.myspring.beans.factory.ConfigurableListableBeanFactory;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.beans.factory.config.BeanFactoryPostProcessor;

/**
 * Description:
 * date: 2021/8/10 16:07
 * Package: com.learn.test.common
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        propertyValues.addPropertyValue(new PropertyValue("company", "阿里巴巴"));
    }
}
