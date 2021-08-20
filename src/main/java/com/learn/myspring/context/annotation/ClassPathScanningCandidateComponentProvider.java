package com.learn.myspring.context.annotation;

import cn.hutool.core.util.ClassUtil;
import com.learn.myspring.beans.factory.config.BeanDefinition;
import com.learn.myspring.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description:
 * date: 2021/8/20 13:27
 * Package: com.learn.myspring.context.annotation
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 处理对象扫描装配
public class ClassPathScanningCandidateComponentProvider {

    /**
     * 这里先要提供一个可以通过配置路径 basePackage=com.learn.myspring.test.bean
     * 解析出 classes 信息的工具方法 findCandidateComponents，
     * 通过这个方法就可以扫描到所有 @Component 注解的 Bean 对象了
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
        for (Class<?> clazz : classes) {
            candidates.add(new BeanDefinition(clazz));
        }
        return candidates;
    }

}
