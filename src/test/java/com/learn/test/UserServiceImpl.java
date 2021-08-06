package com.learn.test;

/**
 * Description:
 * date: 2021/8/3 19:14
 * Package: com.learn.test
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class UserServiceImpl {

    private String name;

    public UserServiceImpl(String name) {
        this.name = name;
    }

    public void queryUserInfo() {
        System.out.println("查询用户信息：" + name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("").append(name);
        return sb.toString();
    }
}
