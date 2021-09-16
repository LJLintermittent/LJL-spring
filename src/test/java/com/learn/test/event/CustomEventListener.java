package com.learn.test.event;

import com.learn.myspring.context.ApplicationListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description:
 * date: 2021/8/17 16:34
 * Package: com.learn.test.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class CustomEventListener implements ApplicationListener<CustomEvent> {

    //线程安全的使用SimpleDateFormat
    private static final ThreadLocal<DateFormat> FORMAT_THREAD_LOCAL =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public void onApplicationEvent(CustomEvent event) {
        System.out.println("收到：" + event.getSource() + "发来的消息，当前时间为；" +
                FORMAT_THREAD_LOCAL.get().format(System.currentTimeMillis()));
        System.out.println("消息是：" + event.getId() + ":" + event.getMessage());
    }
}
