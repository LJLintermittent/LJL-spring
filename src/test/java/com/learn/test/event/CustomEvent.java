package com.learn.test.event;

import com.learn.myspring.context.event.ApplicationContextEvent;

/**
 * Description:
 * date: 2021/8/17 16:32
 * Package: com.learn.test.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */

/**
 * 创建一个自定义事件，在事件类的构造函数中可以添加自己的想要的入参信息。
 * 这个事件类最终会被完成的拿到监听里，所以你添加的属性都会被获得到
 */
public class CustomEvent extends ApplicationContextEvent {

    private Long id;

    private String message;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CustomEvent(Object source, Long id, String message) {
        super(source);
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
