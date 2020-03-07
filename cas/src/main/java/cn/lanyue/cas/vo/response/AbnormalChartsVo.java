package cn.lanyue.cas.vo.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 21:04
 */
@Getter
@Setter
public class AbnormalChartsVo extends ChartsVo {

    private int riskValue;


    private int abnormalValue;


}
