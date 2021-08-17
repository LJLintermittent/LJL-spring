package com.learn.test.event;

import com.learn.myspring.context.ApplicationListener;
import com.learn.myspring.context.event.ContextRefreshedEvent;

/**
 * Description:
 * date: 2021/8/17 16:38
 * Package: com.learn.test.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("刷新事件：" + this.getClass().getName());
    }
}
