package com.learn.myspring.beans.factory.config;

/**
 * Description:
 * date: 2021/8/7 17:52
 * Package: com.learn.myspring.beans.factory.config
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//Bean的引用
public class BeanReference {

    /**
     * BeanReference用于在填充属性的时候
     * 只负责填充Bean类型的属性
     */

    private final String beanName;

    public BeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
