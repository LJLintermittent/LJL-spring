package com.learn.test.unittest;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * date: 2021/8/24 19:04
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class CircleTest {

    /**
     * 此demo仅使用一级缓存来解决最普通场景中的循环依赖问题
     */
    private final static Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    @Test
    public void test() throws Exception {
        System.out.println(getBean(B.class).getA());
        System.out.println(getBean(A.class).getB());
    }

    private static <T> T getBean(Class<T> beanClass) throws Exception {
        String beanName = beanClass.getSimpleName().toLowerCase();
        if (singletonObjects.containsKey(beanName)) {
            return (T) singletonObjects.get(beanName);
        }
        // 实例化对象入缓存
        //反射，默认空参构造
        Object bean = beanClass.newInstance();
        singletonObjects.put(beanName, bean);
        // 属性填充补全对象
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            String fieldBeanName = fieldClass.getSimpleName().toLowerCase();
            field.set(bean, singletonObjects.containsKey(fieldBeanName) ? singletonObjects.get(fieldBeanName)
                    : getBean(fieldClass));
            field.setAccessible(false);
        }
        return (T) bean;
    }

}

class A {

    private B b;

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}

class B {

    private A a;

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }
}
