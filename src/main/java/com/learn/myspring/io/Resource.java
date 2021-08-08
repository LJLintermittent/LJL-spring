package com.learn.myspring.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description:
 * date: 2021/8/8 22:51
 * Package: com.learn.myspring.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface Resource {

    /**
     * Spring的IO包主要用于处理资源加载流
     * 定义好Resource接口，提供获取InputStream流的方法
     * 接下来再分别实现三种不同的流文件操作：classPath、FileSystem、URL
     * 分别是classPath，系统文件，云配置文件
     */
    InputStream getInputStream() throws IOException;


}
