package com.learn.myspring.context.event;


/**
 * Description:
 * date: 2021/8/17 15:24
 * Package: com.learn.myspring.context.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
//监听关闭
public class ContextClosedEvent extends ApplicationContextEvent {

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ContextClosedEvent(Object source) {
        super(source);
    }

}
