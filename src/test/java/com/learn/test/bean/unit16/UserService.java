package com.learn.test.bean.unit16;

import com.learn.myspring.stereotype.Component;

import java.util.Random;

/**
 * Description:
 * date: 2021/8/23 15:20
 * Package: com.learn.test.bean.unit16
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Component("userService")
public class UserService implements IUserService {

    private String token;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "李佳乐-阿里巴巴，" + token;
    }

    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
