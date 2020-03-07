package cn.lanyue.cas.vo.response;


import cn.lanyue.cas.entity.TripRecord;
import cn.lanyue.cas.vo.request.AccessRule;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FamilyAccessRecordAnalysis implements Serializable {

    /**
     * 房号总的进出次数
     */
    private int total;
    /**
     * 今日进的次数
     */
    private int inCount;
    /**
     * 今日出的次数
     */
    private int outCount;
    /**
     * 近3日的进出记录
     */
    List<TripRecord> tripRecords;

    private AccessRule accessRule;

    /**
     * 门岗控制模式
     */
    private String guardControlPattern;

}
