package com.learn.myspring.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description:
 * date: 2021/8/9 22:39
 * Package: com.learn.myspring.core.io
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//系统文件资源读取器
public class FileSystemResource implements Resource {

    /**
     * 通过指定文件路径的方式读取文件信息
     */
    private final File file;

    private final String path;


    public FileSystemResource(File file) {
        this.file = file;
        this.path = file.getPath();
    }

    public FileSystemResource(String path) {
        this.file = new File(path);
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public final String getPath() {
        return this.path;
    }

}
