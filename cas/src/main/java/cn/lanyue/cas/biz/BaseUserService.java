package cn.lanyue.cas.biz;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.lanyue.cas.entity.BaseGroup;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.request.BaseUserInfoParam;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import cn.lanyue.cas.vo.response.LoginUser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.BaseUserMapper;
import cn.lanyue.cas.entity.BaseUser;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户表Service
 */
@Service
@Transactional(readOnly = true)
public class BaseUserService extends BaseBiz<BaseUserMapper, BaseUser> {

    public Optional<BaseUser> findFamilyMainUser(String familyId) {
        BaseUser baseUser = mapper.findFamilyMainUser(familyId);
        return Optional.ofNullable(baseUser);
    }

    public BaseUser findUserByOpenid(String openid) {
        BaseUser baseUser = new BaseUser();
        baseUser.setOpenId(openid);
        return selectOne(baseUser);
    }


    public LoginUser buildLoginUser(WxMaJscode2SessionResult session) {
        Preconditions.checkArgument(Validator.isNotNullOrEmpty(session.getOpenid()),"openid can not be empty");

        BaseUser baseUser = findUserByOpenid(session.getOpenid());

        //加载家庭信息
        if (Validator.isNullOrEmpty(baseUser) ) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(session, loginUser);
            return loginUser;
        }
        List<BaseGroup> groups = mapper.findGroup(baseUser.getId(), null);
        groups.removeAll(Collections.singleton(null));

        if (Validator.isNullOrEmpty(groups)) {
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(session, loginUser);
            return loginUser;
        }

        Map<String, List<BaseGroup>> estate2group = groups.stream().collect(Collectors.groupingBy(BaseGroup::getEstateId));
        List<HousingEstate> estates = Lists.newArrayList();
        for (String estateId : estate2group.keySet()) {
            List<HousingEstate> families = findFamilies(baseUser.getId(), estateId);
            //住户查找家庭
            if (Validator.isNotNullOrEmpty(families)) {
                families.get(0).setGroupCodes(estate2group.get(estateId).stream().map(BaseGroup::getCode).collect(Collectors.toList()));
                estates.addAll(families);
            }else {
                //保安和管理人员查找小区
                List<HousingEstate> estates1 = mapper.findEstates(estateId, baseUser.getId());
                estates1.get(0).setGroupCodes(estate2group.get(estateId).stream().map(BaseGroup::getCode).collect(Collectors.toList()));
                estates.addAll(estates1);
            }
        }

        /*if (Validator.isNotNullOrEmpty(estates)) {
            for (HousingEstate estate : estates) {
                if (Validator.isNotNullOrEmpty(estate2group.get(estate.getId())))
                estate.setGroupCodes(estate2group.get(estate.getId()).stream().map(BaseGroup::getCode).collect(Collectors.toList()));
            }
        }*/

        return LoginUser.build(baseUser, estates, session);
    }


    public List<BaseUser> findFamilyMembers(String familyId) {
        return mapper.findFamilyMembers(familyId);
    }

    public List<HousingEstate> findFamilies(String userId, String estateId) {
        return mapper.findFamilys(userId,estateId);
    }

    public UserBaseInfoVo findBaseUserInfo(BaseUserInfoParam baseUserInfoParam) {

        return mapper.findUserBaseInfo(baseUserInfoParam);
    }

}
