package cn.lanyue.cas.biz;

import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.entity.BaseUserGroup;
import cn.lanyue.cas.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/25 14:43
 */

@Service
public class AuthorizationService {

    @Autowired
    private BaseUserGroupService baseUserGroupService;

    public BaseResponse doAuthorization(String estateId, String userId, String groupId) {

        Example example = new Example(BaseUserGroup.class);
        example.createCriteria().andCondition("attr1=", estateId)
                .andCondition("user_id=", userId)
                .andCondition("group_id=", groupId);
        List<BaseUserGroup> baseUserGroups = baseUserGroupService.selectByExample(example);

        if (Validator.isNullOrEmpty(baseUserGroups) || baseUserGroups.get(0) == null) {
            return new ObjectRestResponse(ExceptionEnum.NO_AUTHORITY);
        }

        return BaseResponse.success();
    }
}
