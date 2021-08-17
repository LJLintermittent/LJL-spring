package com.learn.myspring.context.event;

import com.learn.myspring.context.ApplicationEvent;
import com.learn.myspring.context.ApplicationListener;

/**
 * Description:
 * date: 2021/8/17 15:28
 * Package: com.learn.myspring.context.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
// 事件广播器，定义了添加事件监听和删除监听以及一个广播方法multicastEvent
// 最终推送事件要经过这个multicastEvent方法来处理推送该事件
public interface ApplicationEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     *
     * @param listener the listener to add
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * Remove a listener from the notification list.
     *
     * @param listener the listener to remove
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * Multicast the given application event to appropriate listeners.
     *
     * @param event the event to multicast
     */
    void multicastEvent(ApplicationEvent event);
}
