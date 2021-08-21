package com.learn.test.bean.autowired;

import com.learn.myspring.beans.factory.annotation.Autowired;
import com.learn.myspring.beans.factory.annotation.Value;
import com.learn.myspring.stereotype.Component;

import java.util.Random;

/**
 * Description:
 * date: 2021/8/21 23:01
 * Package: com.learn.test.bean.autowired
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Component("userService")
public class UserService implements IUserService{

    @Value("${token}")
    private String token;

    @Autowired
    private UserDao userDao;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userDao.queryUserName("1") + "，" + token;
    }

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
        return "UserService#token = { " + token + " }";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
