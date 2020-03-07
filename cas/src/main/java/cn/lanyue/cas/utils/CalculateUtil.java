package cn.lanyue.cas.utils;

import java.math.BigDecimal;

/**
 * @Auther: lanyue
 * @Date: 19-8-16
 * @Description: 计算工具类
 */
public class CalculateUtil {

	//随机码字典集
    private static final String RANDOM_STR="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    /**
     * 安全的加法计算
     * @param b1 第一个加数
     * @param bn 多个加数
     * @return 和
     */
    public static BigDecimal safeAdd(BigDecimal b1, BigDecimal... bn) {
        BigDecimal result = b1;
        if (null == result) {
            result = BigDecimal.ZERO;
        }
        if (null != bn) {
            for (BigDecimal b : bn) {
                result = result.add(null == b ? BigDecimal.ZERO : b);
            }
        }

        return result;
    }

    /**
     * 安全的减法计算
     * @param b1 被减数
     * @param bn 多个减数
     * @return 差
     */
    public static BigDecimal safeSubtract(BigDecimal b1, BigDecimal... bn) {
        return safeSubtract(false, b1, bn);

    }

    public static BigDecimal safeSubtract(Boolean isZero, BigDecimal b1, BigDecimal... bn) {
        if (null == b1) {
            b1 = BigDecimal.ZERO;
        }

        BigDecimal result = b1;
        if (null != bn) {
            for (BigDecimal b : bn) {
                result = result.subtract((null == b ? BigDecimal.ZERO : b));
            }
        }

        return isZero ? (result.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : result) : result;
    }

    /**
     * 安全的除法计算
     * @param b1 被除数
     * @param b2 除数
     * @param <T> Number
     * @return 商
     */
    public static <T extends Number> BigDecimal safeDivide(T b1, T b2){
        return safeDivide(b1, b2, 4, BigDecimal.ZERO);
    }

    public static <T extends Number> BigDecimal safeDivide(T b1, T b2, int scale, BigDecimal defaultValue) {
        if (null == b1 ||  null == b2) {
            return defaultValue;
        }
        try {
            return BigDecimal.valueOf(b1.doubleValue()).divide(BigDecimal.valueOf(b2.doubleValue()), scale, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 安全的乘法
     * @param b1 乘数
     * @param b2 乘数
     * @param <T> Number
     * @return 积
     */
    public static <T extends Number> BigDecimal safeMultiply(T b1, T b2) {
        return safeMultiply(b1, b2, 6, BigDecimal.ZERO);
    }

    public static <T extends Number> BigDecimal safeMultiply(T b1, T b2, int scale, BigDecimal defaultValue) {
        if (null == b1 ||  null == b2) {
            return defaultValue;
        }
        try{
            return BigDecimal.valueOf(b1.doubleValue()).multiply(BigDecimal.valueOf(b2.doubleValue())).setScale(scale, BigDecimal.ROUND_HALF_UP);
        }catch (Exception e){
            return defaultValue;
        }
    }

}
