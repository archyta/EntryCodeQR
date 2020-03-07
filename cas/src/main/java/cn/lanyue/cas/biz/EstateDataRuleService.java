package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.EstateDataRuleMapper;
import cn.lanyue.cas.entity.EstateDataRule;

/**
 * 小区数据规则Service
 */
@Service
@Transactional(readOnly = true)
public class EstateDataRuleService extends BaseBiz<EstateDataRuleMapper, EstateDataRule> {

}
