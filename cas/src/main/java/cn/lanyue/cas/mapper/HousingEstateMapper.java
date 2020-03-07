package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.vo.response.EstateUserVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 小区表DAO接口
 */
public interface HousingEstateMapper extends  Mapper<HousingEstate> {

    Integer findEstatePeopleNum(@Param("estateId") String estateId);

    List<EstateUserVo> findEstatePeoples(@Param("estateId") String estateId);

}
