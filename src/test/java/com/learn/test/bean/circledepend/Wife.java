package com.learn.test.bean.circledepend;

/**
 * Description:
 * date: 2021/8/24 18:50
 * Package: com.learn.test.bean.circledepend
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class Wife {

    private Husband husband;
    private IMother mother; // 婆婆

    public String queryHusband() {
        return "Wife.husband、Mother.callMother：" + mother.callMother();
    }

    public Husband getHusband() {
        return husband;
    }

    public void setHusband(Husband husband) {
        this.husband = husband;
    }

    public IMother getMother() {
        return mother;
    }

    public void setMother(IMother mother) {
        this.mother = mother;
    }
}
