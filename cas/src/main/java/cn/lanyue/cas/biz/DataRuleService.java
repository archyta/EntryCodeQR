package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.entity.DataRule;
import cn.lanyue.cas.mapper.DataRuleMapper;
import cn.lanyue.cas.utils.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据规则Service
 */
@Service
@Transactional(readOnly = true)
public class DataRuleService extends BaseBiz<DataRuleMapper, DataRule> {

    public List<DataRule> findDataRuleByEstate(String estateId) {
        List<DataRule> dataRules = mapper.findDataRule(estateId);
        if (Validator.isNullOrEmpty(dataRules)) {
            return Collections.emptyList();
        }
        return dataRules;
    }

}
