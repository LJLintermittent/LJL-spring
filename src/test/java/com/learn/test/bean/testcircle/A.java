package com.learn.test.bean.testcircle;

/**
 * Description:
 * date: 2021/8/25 14:46
 * Package: com.learn.test.bean.testcircle
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class A {

    private B b;

    public A() {
        System.out.println("Bean-A创建成功");
    }


    public void setB(B b) {
        this.b = b;
    }
}
