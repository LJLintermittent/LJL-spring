package com.learn.myspring.beans.factory;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.PropertyValue;
import com.learn.myspring.beans.PropertyValues;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.beans.factory.config.BeanFactoryPostProcessor;
import com.learn.myspring.core.io.DefaultResourceLoader;
import com.learn.myspring.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * Description:
 * date: 2021/8/20 13:26
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 处理占位符配置 ${}占位符,依赖于BeanFactoryPostProcessor的特点：可以在Bean对象实例化之前，改变beanDefinition的属性信息
// 所以这里通过实现BeanFactoryPostProcessor接口，完成对配置文件的加载以及摘取字符串中在属性文件配置的值，把配置信息放到属性配置中
// propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), stringBuffer.toString()));
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private String location;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) {
                        continue;
                    }
                    String strValue = (String) value;
                    StringBuffer stringBuffer = new StringBuffer(strValue);
                    int startIdx = strValue.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
                    int endIdx = strValue.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
                    if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                        String propKey = strValue.substring(startIdx + 2, endIdx);
                        String propVal = properties.getProperty(propKey);
                        stringBuffer.replace(startIdx, endIdx + 1, propVal);
                        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), stringBuffer.toString()));
                    }
                }
            }
        } catch (IOException e) {
            throw new BeansException("Could not load properties", e);
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
