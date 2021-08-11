package com.learn.myspring.context.support;

import com.learn.myspring.beans.factory.support.DefaultListableBeanFactory;
import com.learn.myspring.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * Description:
 * date: 2021/8/10 14:18
 * Package: com.learn.myspring.context.support
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 上下文中对配置信息的加载
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // XmlBeanDefinitionReader处理关于XML文件的配置信息
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);
        String[] configLocations = getConfigLocations();
        if (null != configLocations) {
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    // 又是抽象方法，交给子类去实现， 此方法是为了从入口上下文类，拿到配置信息的地址描述
    protected abstract String[] getConfigLocations();

}
