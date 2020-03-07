package cn.lanyue.cas.biz;

import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.HousingEstate;
import static cn.lanyue.cas.utils.Distinct.distinctByKey;
import cn.lanyue.cas.utils.Validator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.BaseUserGroupMapper;
import cn.lanyue.cas.entity.BaseUserGroup;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户-用户组表Service
 */
@Service

public class BaseUserGroupService extends BaseBiz<BaseUserGroupMapper, BaseUserGroup> {

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private BaseUserService baseUserService;

    @Transactional(readOnly = false)
    public void addUserGroup(String groupId, String userId, String estateId) {
        Example example = new Example(BaseUserGroup.class);
        example.createCriteria().andCondition("group_id=", groupId)
                .andCondition("user_id=", userId)
                .andCondition("attr1=", estateId);

        if (Validator.isNullOrEmpty(selectByExample(example))){
            BaseUserGroup baseUserGroup = new BaseUserGroup();
            baseUserGroup.setUserId(userId);
            baseUserGroup.setGroupId(groupId);
            baseUserGroup.setAttr1(estateId);
            insertSelective(baseUserGroup);
        }
    }

    public List<BaseUser> findEstateManagers(String estateId) {
        List<BaseUser> managers = Lists.newArrayList();

        HousingEstate housingEstate = housingEstateService.selectById(estateId);

        List<BaseUser> estateUserGroup = mapper.findEstateUserGroup(Constant.PROPERTY_ID, estateId, housingEstate.getCrtBy());
        if (Validator.isNotNullOrEmpty(estateUserGroup)) {
            managers.addAll(estateUserGroup);
        }

        if (Validator.isNotNullOrEmpty(housingEstate)) {
            BaseUser baseUser = baseUserService.selectById(housingEstate.getCrtBy());
            managers.add(baseUser);
            managers = managers.stream().filter(u -> u != null && StringUtils.isNotBlank(u.getId()))
                    .filter(distinctByKey(BaseUser::getId)).collect(Collectors.toList());
        }

        return managers;
    }

}
