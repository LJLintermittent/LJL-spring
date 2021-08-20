package com.learn.test.bean.annotation;

import com.learn.myspring.stereotype.Component;

import java.util.Random;

/**
 * Description:
 * date: 2021/8/20 14:34
 * Package: com.learn.test.bean.annotation
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Component("userService")
public class UserService implements IUserService {

    private String token;

    @Override
    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "李佳乐在杭州阿里巴巴工作";
    }

    @Override
    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }

    @Override
    public String toString() {
        return "UserService#token = {" + token + "}";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
