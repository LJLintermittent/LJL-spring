package com.learn.test.bean;

/**
 * Description:
 * date: 2021/8/16 15:12
 * Package: com.learn.test.bean
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class UserServiceForFactory {

    private String uId;

    private String company;

    private String location;

    private IUserDao userDao;

    public String queryUserInfo() {
        return userDao.queryUserName(uId) + "在" + location + "" + company;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public IUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }
}
