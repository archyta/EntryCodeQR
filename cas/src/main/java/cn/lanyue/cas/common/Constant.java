package cn.lanyue.cas.common;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Constant {

    /**
     * 进小区
     */
    public static final String IN = "0";

    /**
     * 出小区
     */
    public static final String OUT = "1";


    public static final String OWNER_ID = "1";

    public static final String GUARD_ID = "2";

    public static final String PROPERTY_ID = "3";

    public static final String accessType = "accessType";

    public static final String accessDays = "accessDays";

    public static final String accessCount = "accessCount";

    public static final Map<String, String> accessTypeMap = ImmutableMap.of(
                                                                "family","本户",
                                                                "oneself","本户",
                                                                "householder","业主"
                                                        );

    /**
     * 体温异常标识
     */
    public static final String normal = "0";
    public static final String risk = "1";
    public static final String abnormal = "2";


    public static final String WEEK = "week";
    public static final String MONTH = "month";
    public static final String TODAY = "today";
    public static final String LAST_MONTH = "lastMonth";

}
