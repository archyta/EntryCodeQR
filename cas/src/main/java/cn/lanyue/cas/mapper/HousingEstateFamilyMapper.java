package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.HousingEstateFamily;
import tk.mybatis.mapper.common.Mapper;

/**
 * 家庭表DAO接口
 */
public interface HousingEstateFamilyMapper extends  Mapper<HousingEstateFamily> {

    HousingEstateFamily findFamilyMembers(HousingEstateFamily housingEstateFamily);
}
