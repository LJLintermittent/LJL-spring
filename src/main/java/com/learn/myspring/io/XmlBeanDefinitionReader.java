package com.learn.myspring.io;

import com.learn.myspring.core.BeanDefinitionRegistry;
import com.learn.myspring.core.BeansException;

/**
 * Description:
 * date: 2021/8/9 23:46
 * Package: com.learn.myspring.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//解析XML处理Bean注册
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader{

    protected XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {

    }

    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {

    }

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {

    }
}
