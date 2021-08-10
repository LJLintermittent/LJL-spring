package com.learn.myspring.core.io;

import cn.hutool.core.lang.Assert;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Description:
 * date: 2021/8/9 23:01
 * Package: com.learn.myspring.core.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//获取资源的包装类的实现
public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 将三种不同类型的资源处理方式进行了包装，判断分别为classpath，url和file
     * 这里不会让外部调用者知道过多的细节，而只关心具体调用的结果
     */
    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            //substring()光传一个参数，表示从这个位置开始截取，beginIndex
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                return new FileSystemResource(location);
            }
        }
    }
}
