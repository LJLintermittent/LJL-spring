package com.learn.myspring.context.event;

import com.learn.myspring.context.ApplicationContext;
import com.learn.myspring.context.ApplicationEvent;

/**
 * Description:
 * date: 2021/8/17 15:23
 * Package: com.learn.myspring.context.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
//ApplicationContextEvent是定义事件的抽象类，所有事件包括关闭，刷新，以及用户自己实现的事件，都需要继承这个类
public class ApplicationContextEvent extends ApplicationEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationContextEvent(Object source) {
        super(source);
    }

    /**
     * Get the <code>ApplicationContext</code> that the event was raised for.
     */
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }


}
