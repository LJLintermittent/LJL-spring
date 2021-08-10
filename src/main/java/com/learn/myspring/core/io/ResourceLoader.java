package com.learn.myspring.core.io;


/**
 * Description:
 * date: 2021/8/9 23:00
 * Package: com.learn.myspring.core.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//获取资源接口，里面传递location地址即可
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
