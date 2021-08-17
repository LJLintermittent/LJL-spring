package com.learn.test.event;

import com.learn.myspring.context.ApplicationListener;
import com.learn.myspring.context.event.ContextClosedEvent;

/**
 * Description:
 * date: 2021/8/17 16:38
 * Package: com.learn.test.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("关闭事件：" + this.getClass().getName());

    }
}
