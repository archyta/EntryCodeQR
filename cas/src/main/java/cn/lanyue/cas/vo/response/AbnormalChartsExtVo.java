package cn.lanyue.cas.vo.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 21:04
 */
@Getter
@Setter
public class AbnormalChartsExtVo extends ChartsVo {

    //小区设置风险体温
    private BigDecimal estateRiskTemperature;
    //小区设置异常体温
    private BigDecimal estateAbnormalTemperature;

    private List<AbnormalChartsVo> abnormalCharts;

}
