package cn.lanyue.cas.biz;

import cn.lanyue.cas.vo.response.EstateUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.HousingEstateMapper;
import cn.lanyue.cas.entity.HousingEstate;

import java.util.List;

/**
 * 小区表Service
 */
@Service
@Transactional(readOnly = true)
public class HousingEstateService extends BaseBiz<HousingEstateMapper, HousingEstate> {


    public Integer findEstatePeopleNum(String estateId) {
        return mapper.findEstatePeopleNum(estateId);
    }


    public List<EstateUserVo> findEstatePeoples(String estateId) {
        return mapper.findEstatePeoples(estateId);
    }


}
