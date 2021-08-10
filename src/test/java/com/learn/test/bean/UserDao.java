package com.learn.test.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/10 15:37
 * Package: com.learn.test.bean
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class UserDao {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("1", "aaa");
        map.put("2", "bbb");
        map.put("3", "ccc");
    }

    public String queryUserName(String uId) {
        return map.get(uId);
    }
}
