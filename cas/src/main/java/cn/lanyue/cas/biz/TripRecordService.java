package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.utils.Query;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.TripRecordMapper;
import cn.lanyue.cas.entity.TripRecord;

import java.util.List;

/**
 * 出行记录表Service
 */
@Service
@Transactional(readOnly = true)
public class TripRecordService extends BaseBiz<TripRecordMapper, TripRecord> {

    public List<TripRecord> findRecords(String familyId, String estateId, Integer days) {
        List<TripRecord> tripRecords = mapper.findRecordByFamily(familyId, estateId, days);
        return tripRecords;
    }

    public List<TripRecord> findRecordsByRule(String accessType, String familyId, String userId, Integer accessDays) {
        List<TripRecord> tripRecords = mapper.findRecordByRule(accessType,familyId, userId, accessDays);
        return tripRecords;
    }


    public List<TripRecord> findRecordForProperty(String estateId, String dataScope) {
        List<TripRecord> tripRecords = mapper.findRecordForProperty(estateId, dataScope);
        return tripRecords;
    }

    public List<TripRecord> highSearch(Query query) {
        return mapper.highSearch(query);
    }


}
