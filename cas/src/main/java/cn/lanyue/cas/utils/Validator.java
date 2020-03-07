package cn.lanyue.cas.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther:
 * @Date: 19-8-25
 * @Description: null校验工具类
 */
public final class Validator {


    public Validator() {
        throw new AssertionError(getClass().getName() + "not can be instances for you!");
    }

    /**
     * 判断对象为空
     * @param value object
     * @return boolean
     */
    public static boolean isNullOrEmpty(Object value) {
        if (null == value){
            return true;
        }
        // 字符串
        if (value instanceof CharSequence){
            return StringUtils.isBlank((CharSequence) value);
        }

        // collections 支持的类型
        if (isCollectionsSupportType(value)){
            return CollectionUtils.sizeIsEmpty(value);
        }
        return false;
    }

    /**
     * 判断对象不为空
     * @param value object
     * @return boolean
     */
    public static boolean isNotNullOrEmpty(Object value){
        return !isNullOrEmpty(value);
    }


    private static boolean isCollectionsSupportType(Object value) {
        // 集合或者map
        boolean isCollectionOrMap = value instanceof Collection || value instanceof Map;

        // 枚举 或者是 Iterator迭代器
        boolean isEnumerationOrIterator = value instanceof Enumeration || value instanceof Iterator;

        return isCollectionOrMap//集合或者map
                || isEnumerationOrIterator//枚举 或者是 Iterator迭代器
                || value.getClass().isArray()//判断数组
                ;
    }


}
