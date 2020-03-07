package cn.lanyue.cas.mapper;

import cn.lanyue.cas.entity.BaseGroup;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.vo.request.BaseUserInfoParam;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import cn.lanyue.cas.entity.BaseUser;

import java.util.List;

/**
 * 用户表DAO接口
 */
public interface BaseUserMapper extends  Mapper<BaseUser> {

    /**
     * 通过openid查找用户的角色
     * @param openId 小程序openid
     * @return 角色列表
     */
    List<BaseGroup> findGroup(@Param("userId") String userId, @Param("estateId") String estateId);

    /**
     * 查询家庭成员信息
     * @param familyId 家庭id
     * @return 成员列表
     */
    List<BaseUser> findFamilyMembers(@Param("familyId") String familyId);

    /**
     * 查询用户所有的家庭信息
     * @param userId 用户id
     * @return 小区信息列表
     */
    List<HousingEstate> findFamilys(@Param("userId") String userId, @Param("estateId")String estateId);

    List<HousingEstate> findEstates(@Param("estateId")String estateId, @Param("userId") String userId);


    BaseUser findFamilyMainUser(@Param("familyId") String familyId);


    UserBaseInfoVo findUserBaseInfo(BaseUserInfoParam baseUserInfoParam);
}
