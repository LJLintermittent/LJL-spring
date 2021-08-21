package com.learn.test.bean.autowired;

import com.learn.myspring.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * date: 2021/8/21 23:03
 * Package: com.learn.test.bean.autowired
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@Component
public class UserDao {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("1", "李佳乐进阿里巴巴");
        map.put("2", "杭州");
        map.put("3", "断续");
    }

    public String queryUserName(String uId) {
        return map.get(uId);
    }
}
