package cn.lanyue.cas.mapper;

import cn.lanyue.cas.core.utils.Query;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import cn.lanyue.cas.entity.TripRecord;

import java.util.List;

/**
 * 出行记录表DAO接口
 */
public interface TripRecordMapper extends  Mapper<TripRecord> {

    /**
     *
     * @param familyId 家庭id
     * @param days 天数
     * @return 记录列表
     */
    List<TripRecord> findRecordByFamily(@Param("familyId") String familyId,
                                        @Param("estateId") String estateId,
                                        @Param("days") Integer days);

    List<TripRecord> findRecordByRule(@Param("accessType") String accessType,
                                      @Param("familyId") String familyId,
                                        @Param("userId") String userId,
                                        @Param("accessDays") Integer accessDays);


    List<TripRecord> findRecordForProperty(@Param("estateId") String estateId, @Param("dataScope") String dataScope);


    List<TripRecord> highSearch(Query query);
}
