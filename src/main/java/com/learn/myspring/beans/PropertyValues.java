package com.learn.myspring.beans;


import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * date: 2021/8/7 17:31
 * Package: com.learn.myspring.beans
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
//Bean的属性集合
public class PropertyValues {
    /**
     * 此类和PropertyValue的作用是创建出一个用于传递类中属性信息的类，因为属性可能会有很多，
     * 所以还需要一个集合包来装下
     */

    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValueList.add(propertyValue);
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue propertyValue : this.propertyValueList) {
            if (propertyValue.getName().equals(propertyName)) {
                return propertyValue;
            }
        }
        return null;
    }

}
