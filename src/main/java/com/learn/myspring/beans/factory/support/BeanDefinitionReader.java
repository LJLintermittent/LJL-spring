package com.learn.myspring.beans.factory.support;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.core.io.Resource;
import com.learn.myspring.core.io.ResourceLoader;

/**
 * Description:
 * date: 2021/8/9 23:20
 * Package: com.learn.myspring.beans.factory.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//Bean定义读取接口
public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    void loadBeanDefinitions(Resource resource) throws BeansException;

    void loadBeanDefinitions(Resource... resources) throws BeansException;

    void loadBeanDefinitions(String location) throws BeansException;

    void loadBeanDefinitions(String... locations) throws BeansException;
}
