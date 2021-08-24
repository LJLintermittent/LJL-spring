package com.learn.test.bean.circledepend;

/**
 * Description:
 * date: 2021/8/24 18:49
 * Package: com.learn.test.bean.circledepend
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class Husband {

    private Wife wife;

    public String queryWife(){
        return "Husband.wife";
    }

    public Wife getWife() {
        return wife;
    }

    public void setWife(Wife wife) {
        this.wife = wife;
    }
}
