package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.HousingEstateFamilyMapper;
import cn.lanyue.cas.entity.HousingEstateFamily;

/**
 * 家庭表Service
 */
@Service
@Transactional(readOnly = true)
public class HousingEstateFamilyService extends BaseBiz<HousingEstateFamilyMapper, HousingEstateFamily> {

    /**
     * 根据 小区、楼栋、单元、房号查找该房屋的所有家庭成员
     * @param housingEstateFamily
     * @return
     */
    public HousingEstateFamily findFamilyMembers(HousingEstateFamily housingEstateFamily) {
        return mapper.findFamilyMembers(housingEstateFamily);
    }
}
