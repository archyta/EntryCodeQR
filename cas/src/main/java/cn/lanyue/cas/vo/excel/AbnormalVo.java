package cn.lanyue.cas.vo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/26 22:50
 */
@Getter
@Setter
public class AbnormalVo implements Serializable {

    @Excel(name = "姓名", height = 15, width = 30, orderNum = "1")
    private String userName;
    @Excel(name = "电话", height = 15, width = 30, orderNum = "2")
    private String mobilePhone;
    @Excel(name = "家庭住址", height = 15, width = 30, orderNum = "3")
    private String address;
    @Excel(name = "体温 (℃)", height = 15, width = 30, orderNum = "4")
    private BigDecimal temperature;
    @Excel(name = "上报时间", height = 15, width = 30, orderNum = "5", format = "yyyy-MM-dd HH:mm:ss")
    private Date timeInterval;



}
