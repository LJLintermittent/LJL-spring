package com.learn.myspring.context;

import com.learn.myspring.beans.BeansException;
import com.learn.myspring.beans.factory.Aware;

/**
 * Description:
 * date: 2021/8/15 14:39
 * Package: com.learn.myspring.context
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
//Interface to be implemented by any object that wishes to be notifiedof the {@link ApplicationContext} that it runs in.
//实现此接口，即能感知到所属的 ApplicationContext
public interface ApplicationContextAware extends Aware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
