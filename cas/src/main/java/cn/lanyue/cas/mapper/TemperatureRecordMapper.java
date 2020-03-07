package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.TemperatureRecord;
import cn.lanyue.cas.vo.response.EstateUserVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 体温登记DAO接口
 */
public interface TemperatureRecordMapper extends  Mapper<TemperatureRecord> {


    List<TemperatureRecord> findTemperatureRecordsByfamilyId(@Param("familyId") String familyId,
                                                             @Param("days") Integer days);


    TemperatureRecord matchingRecord(TemperatureRecord temperatureRecord);

    List<TemperatureRecord> estateAbnormalCount(
                                @Param("estateId") String estateId,
                                @Param("abnormalVal") BigDecimal abnormalVal,
                                @Param("dataScope") String dataScope);

    List<TemperatureRecord> estateReportCount(
                                @Param("estateId") String estateId,
                                @Param("dataScope") String dataScope);

    List<TemperatureRecord> estateAbnormalMax(
                                @Param("estateId") String estateId,
                                @Param("dataScope") String dataScope);

    List<EstateUserVo> estateTemperatureSearch(
                                        @Param("estateId") String estateId,
                                        @Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("temperature") BigDecimal temperature,
                                        @Param("timeInterval") String timeInterval
                                        );
}
