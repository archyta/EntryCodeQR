package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.DataRule;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 数据规则DAO接口
 */
public interface DataRuleMapper extends  Mapper<DataRule> {

    List<DataRule> findDataRule(@Param("estateId") String estateId);
}
