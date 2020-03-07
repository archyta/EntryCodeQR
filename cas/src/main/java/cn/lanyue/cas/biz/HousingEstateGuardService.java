package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.entity.BaseEntity;
import cn.lanyue.cas.utils.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.HousingEstateGuardMapper;
import cn.lanyue.cas.entity.HousingEstateGuard;

/**
 * 小区保安表Service
 */
@Service
@Transactional(readOnly = true)
public class HousingEstateGuardService extends BaseBiz<HousingEstateGuardMapper, HousingEstateGuard> {

    public void addGuard(String estateId, String userId) {
        HousingEstateGuard housingEstateGuard = new HousingEstateGuard();
        housingEstateGuard.setEstateId(estateId);
        housingEstateGuard.setUserId(userId);
        housingEstateGuard.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);
        HousingEstateGuard db = selectOne(housingEstateGuard);
        if (Validator.isNullOrEmpty(db)) {
            insertSelective(housingEstateGuard);
        }
    }
}
