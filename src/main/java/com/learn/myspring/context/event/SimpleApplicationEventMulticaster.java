package com.learn.myspring.context.event;

import com.learn.myspring.beans.factory.BeanFactory;
import com.learn.myspring.context.ApplicationEvent;
import com.learn.myspring.context.ApplicationListener;

/**
 * Description:
 * date: 2021/8/17 16:12
 * Package: com.learn.myspring.context.event
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster{

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @SuppressWarnings("all")
    @Override
    public void multicastEvent(final ApplicationEvent event) {
        for (final ApplicationListener listener : getApplicationListeners(event)) {
            listener.onApplicationEvent(event);
        }
    }
}
