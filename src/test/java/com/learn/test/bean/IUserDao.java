package com.learn.test.bean;

/**
 * Description:
 * date: 2021/8/16 15:09
 * Package: com.learn.test.bean
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 之所以用IUserDao是为了通过FactoryBean做一个自定义对象的代理操作
public interface IUserDao {

    String queryUserName(String uId);

}
