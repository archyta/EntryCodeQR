package cn.lanyue.cas.biz;


import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.cache.CacheManagerService;
import cn.lanyue.cas.core.cache.CacheName;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.msg.TableResultResponse;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.entity.*;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.enums.AccessStatus;
import cn.lanyue.cas.vo.request.AccessRule;
import cn.lanyue.cas.vo.request.HousingEstateCreate;
import cn.lanyue.cas.vo.request.ManagerVo;
import cn.lanyue.cas.vo.response.ChartsVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static cn.lanyue.cas.core.cache.CacheType.getRegistType;


/**
 * 门卫服务
 */
@Service
public class PropertyService {

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private HousingEstateGuardService housingEstateGuardService;

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private StreetOfficeService streetOfficeService;

    @Autowired
    private TripRecordService tripRecordService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private BaseUserGroupService baseUserGroupService;

    @Resource
    private EstateDataRuleService estateDataRuleService;

    @Resource
    private DataRuleService dataRuleService;

    @Autowired
    CacheManagerService cacheManagerService;

    @Autowired
    private TemperatureRecordService temperatureRecordService;


    /**
     * 新增管理员
     * @param managerVo
     * @return
     */
    @Transactional(readOnly = false)
    public BaseResponse addManager(ManagerVo managerVo) {
        HousingEstate housingEstate = housingEstateService.selectById(managerVo.getEstateId());
        if (housingEstate == null) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR, "小区id不存在");
        }
        baseUserGroupService.addUserGroup(Constant.PROPERTY_ID, managerVo.getUserId(), managerVo.getEstateId());
        return BaseResponse.success();
    }

    /**
     * 删除管理人员
     * @param managerVo
     * @return
     */
    @Transactional(readOnly = false)
    public BaseResponse delManager(ManagerVo managerVo) {
        HousingEstate housingEstate = housingEstateService.selectById(managerVo.getEstateId());
        if (housingEstate == null) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR, "小区id不存在");
        }
        Example example = new Example(BaseUserGroup.class);
        example.createCriteria()
                .andCondition("attr1=", managerVo.getEstateId())
                .andCondition("user_id=", managerVo.getUserId());

        List<BaseUserGroup> baseUserGroups = baseUserGroupService.selectByExample(example);

        if (Validator.isNullOrEmpty(baseUserGroups)) {
            return BaseResponse.success();
        }

        String delId = "";
        for (BaseUserGroup baseUserGroup : baseUserGroups) {
            if (Constant.PROPERTY_ID.equals(baseUserGroup.getGroupId())) {
                delId = baseUserGroup.getId();
                break;
            }
        }
        baseUserGroupService.deleteById(delId);
        return BaseResponse.success();
    }


    /**
     * 删除保安
     * @param managerVo
     * @return
     */
    public BaseResponse delGuard(ManagerVo managerVo) {
        Example example = new Example(HousingEstateGuard.class);
        example.createCriteria().andCondition("estate_id=", managerVo.getEstateId())
                .andCondition("user_id=", managerVo.getUserId());
        housingEstateGuardService.deleteByExample(example);

        Example groupExample = new Example(BaseUserGroup.class);
        groupExample.createCriteria()
                .andCondition("user_id=",managerVo.getEstateId())
                .andCondition("attr1=",managerVo.getEstateId())
                .andCondition("group_id=",Constant.PROPERTY_ID);
        List<BaseUserGroup> baseUserGroups = baseUserGroupService.selectByExample(groupExample);

        if (Validator.isNullOrEmpty(baseUserGroups)) {
            Example example1 = new Example(BaseUserGroup.class);
            example1.createCriteria().andCondition("user_id=", managerVo.getUserId())
                    .andCondition("group_id=", Constant.GUARD_ID)
                    .andCondition("attr1=", managerVo.getEstateId());
            baseUserGroupService.deleteByExample(example1);
        }
        return BaseResponse.success();
    }



    /**
     * 新增小区基础信息
     * @param housingEstateCreate 小区实体
     * @return 小区实体
     */
    @Transactional(readOnly = false)
    public BaseResponse addBaseInfo(HousingEstateCreate housingEstateCreate) {

        //校验验证码
        boolean isPass = smsService.verifySmsCode(  housingEstateCreate.getOpenId(),
                                                    housingEstateCreate.getResponsibleMobilePhone(),
                                                    housingEstateCreate.getVerifyCode()
        );

        if (!isPass) {
            return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, housingEstateCreate);
        }

        BaseUser baseUser = housingEstateCreate.buildBaseUser();
        HousingEstate housingEstate = housingEstateCreate.buildEstate();

        //查询用户表
        BaseUser dbUser = baseUserService.findUserByOpenid(housingEstateCreate.getOpenId());

        if (dbUser == null) {
            baseUserService.insertSelective(baseUser);
        }else {
           baseUser = dbUser;
        }

        //小区
        housingEstate.setCrtBy(baseUser.getId());
        housingEstateService.insertSelective(housingEstate);

        //保存用户组
        baseUserGroupService.addUserGroup(Constant.PROPERTY_ID, baseUser.getId(), housingEstate.getId());

        //创建默认的规则
        updateAccessRule(AccessRule.transformAccessRule(DataRule.defaultEstateConfig(), false));

        cacheManagerService.remove(CacheName.SMS, getRegistType(housingEstateCreate.getOpenId(), housingEstateCreate.getResponsibleMobilePhone()));
        housingEstateCreate.setId(housingEstate.getId());
        return new ObjectRestResponse().data(housingEstateCreate);
    }

    @Transactional(readOnly = false)
    public BaseResponse update(HousingEstateCreate housingEstateCreate) {

        //校验验证码
        boolean isPass = smsService.verifySmsCode(  housingEstateCreate.getOpenId(),
                housingEstateCreate.getResponsibleMobilePhone(),
                housingEstateCreate.getVerifyCode()
        );

        if (!isPass) {
            return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, housingEstateCreate);
        }

        BaseUser baseUser = housingEstateCreate.buildBaseUser();
        HousingEstate housingEstate = housingEstateCreate.buildEstate();
        housingEstate.setId(housingEstateCreate.getId());

        Example example = new Example(BaseUser.class);
        example.createCriteria().andCondition("open_id=", baseUser.getOpenId());

        baseUserService.updateByExampleSelective(baseUser, example);

        //更新小区海报
        housingEstate.setGuardPoster("");
        housingEstate.setOwnerPoster("");
        housingEstateService.updateSelectiveById(housingEstate);

        return new ObjectRestResponse().data(housingEstate);
    }

    @Transactional(readOnly = false)
    public void updateAccessRule(AccessRule accessRule) {
        Example example = new Example(EstateDataRule.class);
        example.createCriteria().andCondition("estate_id=", accessRule.getEstateId());
        List<EstateDataRule> estateDataRules = estateDataRuleService.selectByExample(example);
        estateDataRuleService.deleteByExample(example);

        if (Validator.isNotNullOrEmpty(estateDataRules)){
            Example ruleExa = new Example(DataRule.class);
            ruleExa.createCriteria().andIn("id", estateDataRules.stream().map(EstateDataRule::getRuleId).collect(Collectors.toList()));
            dataRuleService.deleteByExample(ruleExa);
        }

        List<DataRule> dataRules = accessRule.transformDataRule();
        dataRules.forEach(dataRule -> {
            dataRuleService.insertSelective(dataRule);

            EstateDataRule estateDataRule = new EstateDataRule();
            estateDataRule.setEstateId(accessRule.getEstateId());
            estateDataRule.setRuleId(dataRule.getId());

            estateDataRuleService.insertSelective(estateDataRule);
        });
    }

    public AccessRule getAccessRules(String estateId) {
        Example example = new Example(EstateDataRule.class);
        example.createCriteria().andCondition("estate_id=", estateId);
        List<EstateDataRule> estateDataRules = estateDataRuleService.selectByExample(example);

        if (Validator.isNotNullOrEmpty(estateDataRules)){
            Example ruleExa = new Example(DataRule.class);
            ruleExa.createCriteria().andIn("id", estateDataRules.stream().map(EstateDataRule::getRuleId).collect(Collectors.toList()));
            return AccessRule.transformAccessRule(dataRuleService.selectByExample(ruleExa), false);
        }
        return null;
    }


    public HousingEstate findOne(String estateId) {
        HousingEstate ret = housingEstateService.selectById(estateId);
        if (ret == null) {
            return null;
        }
        StreetOffice streetOffice = streetOfficeService.selectById(ret.getStreetOfficeId());
        if (streetOffice != null) {
            ret.setStreetOfficeName(streetOffice.getName());
        }
        //查找本小区有多少个保安
        Example example = new Example(HousingEstateGuard.class);
        example.createCriteria().andCondition("estate_id=", estateId);
        int i = housingEstateGuardService.selectCountByExample(example);
        ret.setGuardNum(i);

        BaseUser baseUser = baseUserService.selectById(ret.getCrtBy());
        ret.setCrtUser(baseUser);

        return ret;
    }

    /**
     * 查询某小区的门卫人员列表
     * @param estateId 小区id
     * @return 人员列表
     */
    public List<BaseUser> guards(String estateId) {
        HousingEstateGuard query = new HousingEstateGuard();
        query.setEstateId(Validator.isNullOrEmpty(estateId) ? "":estateId);
        List<HousingEstateGuard> housingEstateGuards = housingEstateGuardService.selectList(query);

        if (Validator.isNullOrEmpty(housingEstateGuards)) {
            return Collections.emptyList();
        }

        List<String> userIds = housingEstateGuards.stream()
                .filter(h-> Validator.isNotNullOrEmpty(h.getUserId()))
                .map(HousingEstateGuard::getUserId)
                .collect(Collectors.toList());

        Example example = new Example(BaseUser.class);
        example.createCriteria().andIn("id", userIds);
        List<BaseUser> baseUsers = baseUserService.selectByExample(example);

        Example example1 = new Example(BaseUserGroup.class);
        example1.createCriteria()
                .andCondition("attr1=", estateId)
                .andCondition("group_id=", Constant.PROPERTY_ID);
        List<BaseUserGroup> baseUserGroups = baseUserGroupService.selectByExample(example1);
        if (Validator.isNotNullOrEmpty(baseUserGroups)) {
            List<String> collect = baseUserGroups.stream().map(BaseUserGroup::getUserId).collect(Collectors.toList());
            for (BaseUser baseUser : baseUsers) {
                if (collect.contains(baseUser.getId())) {
                    baseUser.setManager(true);
                }
            }
        }
        return baseUsers;
    }


    public Map<String, Object> recordTotal(String estateId, String dataScope ) {
        Map<String, Object> retMap = Maps.newHashMap();
        retMap.put("abnormal","");
        retMap.put("back","");
        List<TripRecord> tripRecords = tripRecordService.findRecordForProperty(estateId, dataScope);
        if (Validator.isNotNullOrEmpty(tripRecords)) {
            Map<Boolean, Long> collect =
                    tripRecords.stream().collect(Collectors.groupingBy(t -> AccessStatus.countInoutFilter(t.getStatus()), Collectors.counting()));
            retMap.put("back", collect.get(false));
        }
        Integer abnormalCount = temperatureRecordService.checkEstateAbnormalCount(estateId, dataScope);

        if (abnormalCount != null) {
            retMap.put("abnormal",abnormalCount);
        }
        return retMap;
    }


    public List<ChartsVo> inout2charts(String estateId, String dataScope ) {
        List<TripRecord> tripRecords = tripRecordService.findRecordForProperty(estateId, dataScope);

        if (Validator.isNullOrEmpty(tripRecords)) {
            return Collections.emptyList();
        }

        Map<LocalDate, Long> map = tripRecords.stream().filter(t -> AccessStatus.countInoutFilter(t.getStatus()))
                .collect(Collectors.groupingBy(t -> DateUtils.date2LocalDate(t.getCrtTime()), Collectors.counting()));

        List<LocalDate> dates = DateUtils.betweenDays(dataScope);
        dates.forEach(d -> map.merge(d,0L,(k1,k2) -> k1));

        List<ChartsVo> chartsVos = Lists.newArrayListWithCapacity(map.size());
        for (LocalDate localDate : map.keySet()) {
            ChartsVo chartsVo = new ChartsVo();
            chartsVo.setDate(localDate);
            chartsVo.setValue(map.get(localDate));
            chartsVos.add(chartsVo);
        }

        chartsVos = chartsVos.stream().sorted().collect(Collectors.toList());

        return chartsVos;

    }

    public List<ChartsVo> back2charts(String estateId, String dataScope) {
        List<TripRecord> tripRecords = tripRecordService.findRecordForProperty(estateId, dataScope);

        if (Validator.isNullOrEmpty(tripRecords)) {
            return Collections.emptyList();
        }

        Map<LocalDate, Long> map = tripRecords.stream().filter(t -> !AccessStatus.countInoutFilter(t.getStatus()))
                .collect(Collectors.groupingBy(t -> DateUtils.date2LocalDate(t.getCrtTime()), Collectors.counting()));

        List<LocalDate> dates = DateUtils.betweenDays(dataScope);

        dates.forEach(d -> map.merge(d,0L,(k1,k2) -> k1));

        List<ChartsVo> chartsVos = Lists.newArrayListWithCapacity(map.size());
        for (LocalDate localDate : map.keySet()) {
            ChartsVo chartsVo = new ChartsVo();
            chartsVo.setDate(localDate);
            chartsVo.setValue(map.get(localDate));
            chartsVos.add(chartsVo);
        }

        chartsVos = chartsVos.stream().sorted().collect(Collectors.toList());

        return chartsVos;

    }


    public TableResultResponse<TripRecord> highSearch(Query query) {
        Page<Object> objects = PageHelper.startPage(query.getPage(), query.getLimit());
        List<TripRecord> tripRecords = tripRecordService.highSearch(query);
        return new TableResultResponse(objects.getTotal(), tripRecords);
    }
}
