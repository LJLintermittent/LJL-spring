package com.learn.myspring.version2;

/**
 * Description:
 * date: 2021/8/5 18:41
 * Package: com.learn.myspring.day2
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class BeanDefinition {

    /**
     * 在Bean的定义中，把Object Bean替换为了Class，这样就可以把Bean的实例化操作放到容器中处理
     */
    private Class beanClass;

    public BeanDefinition(Class beanClass) {
        this.beanClass = beanClass;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }
}
