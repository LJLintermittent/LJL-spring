package com.learn.test.bean.testcircle;

/**
 * Description:
 * date: 2021/8/25 14:47
 * Package: com.learn.test.bean.testcircle
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class B {

    private A a;

    public B() {
        System.out.println("Bean-B创建成功");
    }

    public void setA(A a) {
        this.a = a;
    }
}
