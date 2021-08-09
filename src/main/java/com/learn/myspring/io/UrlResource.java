package com.learn.myspring.io;

import cn.hutool.core.lang.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Description:
 * date: 2021/8/9 22:43
 * Package: com.learn.myspring.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//云配置文件读取器
public class UrlResource implements Resource {

    private final URL url;

    public UrlResource(URL url) {
        //Assert：hutool工具包中的断言检查，经常用于做变量检查
        Assert.notNull(url, "URL must not be null");
        this.url = url;
    }

    /**
     * 通过 HTTP 的方式读取云服务的文件
     * 可以把文件放在github上
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection = this.url.openConnection();
        try {
            return urlConnection.getInputStream();
        } catch (IOException exception) {
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
            throw exception;
        }
    }
}
