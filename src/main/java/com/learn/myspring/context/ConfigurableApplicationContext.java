package com.learn.myspring.context;

import com.learn.myspring.beans.BeansException;

/**
 * Description:
 * date: 2021/8/10 14:15
 * Package: com.learn.myspring.context
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 刷新容器
     */
    void refresh() throws BeansException;

}


