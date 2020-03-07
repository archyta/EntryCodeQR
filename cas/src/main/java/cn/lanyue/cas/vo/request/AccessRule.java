package cn.lanyue.cas.vo.request;

import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.validation.UpdateGroup;
import cn.lanyue.cas.entity.DataRule;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/18 22:10
 */
@Getter
@Setter
public class AccessRule {

    @NotBlank(groups = {UpdateGroup.class})
    private String estateId;
    @NotBlank(groups = {UpdateGroup.class})
    private String accessType;
    @NotBlank(groups = {UpdateGroup.class})
    private String accessDays;
    @NotBlank(groups = {UpdateGroup.class})
    private String accessCount;


    public List<DataRule> transformDataRule() {
        List<DataRule> dataRules = Lists.newArrayListWithCapacity(3);

        DataRule type = new DataRule();
        type.setType(Constant.accessType);
        type.setValue(this.accessType);

        DataRule days = new DataRule();
        days.setType(Constant.accessDays);
        days.setValue(this.accessDays);

        DataRule count = new DataRule();
        count.setType(Constant.accessCount);
        count.setValue(this.accessCount);

        dataRules.add(type);
        dataRules.add(days);
        dataRules.add(count);

        return dataRules;
    }

    public static AccessRule transformAccessRule(List<DataRule> dataRules, boolean isTransformZN) {

        AccessRule accessRule = new AccessRule();

        for (DataRule dataRule : dataRules) {
            if (dataRule.getType().equals(Constant.accessType)) {
                accessRule.setAccessType(isTransformZN ? Constant.accessTypeMap.get(dataRule.getValue()) : dataRule.getValue());
            }
            if (dataRule.getType().equals(Constant.accessDays)) {
                accessRule.setAccessDays(dataRule.getValue());
            }
            if (dataRule.getType().equals(Constant.accessCount)) {
                accessRule.setAccessCount(dataRule.getValue());
            }
        }
        return accessRule;
    }

}
