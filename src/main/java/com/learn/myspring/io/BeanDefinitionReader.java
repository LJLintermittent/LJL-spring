package com.learn.myspring.io;

import com.learn.myspring.core.BeanDefinitionRegistry;
import com.learn.myspring.core.BeansException;

/**
 * Description:
 * date: 2021/8/9 23:20
 * Package: com.learn.myspring.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//Bean定义读取接口
public interface BeanDefinitionReader {

    // Bean定义的注册
    BeanDefinitionRegistry getRegistry();

    // 资源加载器，以上两个方法都是提供给后面三个方法的工具，
    // 这两个方法的实现会包装到抽象类中，以免污染具体的接口实现方法
    ResourceLoader getResourceLoader();

    void loadBeanDefinitions(Resource resource) throws BeansException;

    void loadBeanDefinitions(Resource... resources) throws BeansException;

    void loadBeanDefinitions(String location) throws BeansException;

}
