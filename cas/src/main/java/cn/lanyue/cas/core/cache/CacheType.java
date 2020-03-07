package cn.lanyue.cas.core.cache;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/18 9:59
 */
public class CacheType {

    public static final String regist = "regist";


    public static String getRegistType(String str, String phone) {
        return StringUtils.join(regist, str, phone);
    }
}
