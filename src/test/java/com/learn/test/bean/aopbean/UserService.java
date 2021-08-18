package com.learn.test.bean.aopbean;

import java.util.Random;

/**
 * Description:
 * date: 2021/8/18 17:25
 * Package: com.learn.test.bean.aopbean
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class UserService implements IUserService{

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "李佳乐，1，杭州";
    }

    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }
}
