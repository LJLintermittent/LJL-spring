package com.learn.myspring.beans.factory;

/**
 * Description:
 * date: 2021/8/13 14:19
 * Package: com.learn.myspring.beans.factory
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
// 定义销毁方法的接口
// InitializingBean、DisposableBean，两个接口方法还是比较常用的，
// 在一些需要结合 Spring 实现的组件中，经常会使用这两个方法来做一些参数的初始化和销毁操作。
// 比如接口暴漏、数据库数据读取、配置文件加载
public interface DisposableBean {

    void destroy() throws Exception;

}
