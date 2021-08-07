package com.learn.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/7 18:53
 * Package: com.learn.test
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class UserDao {

    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("1", "aaa");
        map.put("2", "bbb");
        map.put("3", "ccc");
    }

    public String queryUserName(String uid) {
        return map.get(uid);
    }
}
