package com.learn.test.unittest;

import cn.hutool.core.io.IoUtil;
import com.learn.myspring.core.io.DefaultResourceLoader;
import com.learn.myspring.core.io.Resource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description:
 * date: 2021/8/10 0:45
 * Package: com.learn.test.unittest
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class ResourceTest {

    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() {
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    public void test_classpath() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void test_file() throws IOException {
        Resource resource = resourceLoader.getResource("src/main/resources/important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void test_url() throws IOException {
        Resource resource = resourceLoader.
                getResource("https://gitee.com/LJLintermittent/ljl-spring/blob/master/src/main/resources/important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);

    }

}


