package cn.lanyue.cas.vo.response;

import cn.lanyue.cas.entity.TemperatureRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 23:41
 */
@Getter
@Setter
public class EstateUserVo {

    private String id;

    private String userName;


    private String mobilePhone;


    /**
     * 楼栋
     */
    private String building;
    /**
     * 单元
     */
    private String unit;
    /**
     * 房号
     */
    private String roomNumber;


    List<TemperatureRecord> temperatureRecords;




}
