package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.BaseUser;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import cn.lanyue.cas.entity.BaseUserGroup;

import java.util.List;

/**
 * 用户-用户组表DAO接口
 */
public interface BaseUserGroupMapper extends  Mapper<BaseUserGroup> {

    /**
     * 查询小区管理者，非创建者
     * @param groupId 角色id
     * @param estateId 小区id
     * @return 管理者列表
     */
    List<BaseUser> findEstateUserGroup(@Param("groupId")String groupId,
                                       @Param("estateId")String estateId,
                                       @Param("notUserId")String notUserId);
}
