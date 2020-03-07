package cn.lanyue.cas.vo.enums;

import lombok.Getter;

@Getter
public enum AccessStatus {


    IN("0","进门"),
    OUT("1","出门"),
    IN_BACK("2","进门/劝返"),
    OUT_BACK("3","出门/劝返");

    private String code;
    private String message;

    AccessStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     *  只统计进出状态
     * @param code 编码
     * @return 包含进出->true or false
     */
    public static boolean countInoutFilter(String code) {
        return IN.getCode().equals(code) || OUT.getCode().equals(code);
    }

    public static String convertMsg(String code) {
        for (AccessStatus value : AccessStatus.values()) {
            if (value.getCode().equals(code)){
                return value.getMessage();
            }
        }
        throw new RuntimeException("找不到当前的状态定义");
    }

}
