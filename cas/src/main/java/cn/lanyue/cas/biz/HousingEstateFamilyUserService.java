package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.HousingEstateFamilyUserMapper;
import cn.lanyue.cas.entity.HousingEstateFamilyUser;
import tk.mybatis.mapper.entity.Example;

/**
 * 房屋-业主Service
 */
@Service

public class HousingEstateFamilyUserService extends BaseBiz<HousingEstateFamilyUserMapper, HousingEstateFamilyUser> {

    @Transactional(readOnly = false)
    public void addFamilyUser(String familyId, String userId) {
        HousingEstateFamilyUser housingEstateFamilyUser = new HousingEstateFamilyUser();
        housingEstateFamilyUser.setFamilyId(familyId);
        housingEstateFamilyUser.setUserId(userId);
        HousingEstateFamilyUser db = selectOne(housingEstateFamilyUser);
        if (db == null) {
            insertSelective(housingEstateFamilyUser);
        }
    }

    @Transactional(readOnly = false)
    public void updateByExampleSelective(HousingEstateFamilyUser hefu, Example example) {
        mapper.updateByExampleSelective(hefu, example);
    }
}
