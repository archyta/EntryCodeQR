package cn.lanyue.cas.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/28 10:43
 */
@UtilityClass
public class StringUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(((13[0-9])|(15[0-9])|(16[0-9])|(17[3-8])|(18[0-9])|(19[0-9])|(14[5-7]))+\\d{8})$");


    public boolean isMobile(String mobile) {
        Matcher matcher = MOBILE_PATTERN.matcher(mobile);
        return matcher.matches();
    }

    public static void main(String[] args) {
        String mobile = "17377777777";
        boolean mobile1 = isMobile(mobile);
        System.out.println(mobile1);
    }

}
