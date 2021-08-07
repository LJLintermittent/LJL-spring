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
public class UserServiceImpl implements UserService {

    private String uid;

//    private String name;

    //在service中注入Dao，这样就能体现Bean的属性依赖
    private UserDao userDao;

//    public UserServiceImpl(String name) {
//        this.name = name;
//    }

    public void queryUserInfo() {
        System.out.println("查询的用户信息：" + userDao.queryUserName(uid));
//        System.out.println("查询用户信息：" + name);
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder();
//        sb.append("").append(name);
//        return sb.toString();
//    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
