package cn.lanyue.cas.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 23:11
 */
@Setter
@Getter
public class RateChartsVo {

    @JsonFormat(pattern = "MM/dd")
    private LocalDate date;

    private BigDecimal rate;

}
