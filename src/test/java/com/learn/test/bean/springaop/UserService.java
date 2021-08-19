package com.learn.test.bean.springaop;

import java.util.Random;

/**
 * Description:
 * date: 2021/8/19 17:59
 * Package: com.learn.test.bean.springaop
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
        return "李佳乐在杭州阿里巴巴";
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
