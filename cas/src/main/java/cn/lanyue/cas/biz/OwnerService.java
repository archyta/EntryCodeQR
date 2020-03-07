package cn.lanyue.cas.biz;


import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.cache.CacheManagerService;
import cn.lanyue.cas.core.cache.CacheName;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.utils.IdGen;
import cn.lanyue.cas.entity.*;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.enums.AccessStatus;
import cn.lanyue.cas.vo.request.AccessRule;
import cn.lanyue.cas.vo.request.BaseUserInfoParam;
import cn.lanyue.cas.vo.request.ExamineVo;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import cn.lanyue.cas.vo.response.FamilyAccessRecordAnalysis;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.lanyue.cas.core.cache.CacheType.getRegistType;


/**
 * 业主服务
 */
@Service
public class OwnerService {

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private HousingEstateFamilyService housingEstateFamilyService;

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private TripRecordService tripRecordService;

    @Autowired
    private BaseUserGroupService baseUserGroupService;

    @Autowired
    private HousingEstateFamilyUserService housingEstateFamilyUserService;

    @Autowired
    private DataRuleService dataRuleService;

    @Autowired
    private SmsService smsService;

    @Autowired
    CacheManagerService cacheManagerService;

    @Autowired
    private CarService carService;

    @Autowired
    private HealthStatusService healthStatusService;

    @Autowired
    private TemperatureRecordService temperatureRecordService;


    @Transactional(readOnly = false)
    public void delUser(BaseUserInfoParam baseUserInfoParam) {
        //1.删出行记录
        Example tripRecordWhere = new Example.Builder(TripRecord.class)
                .where(WeekendSqls.<TripRecord>custom()
                        .andEqualTo(TripRecord::getFamilyId, baseUserInfoParam.getFamilyId())
                        .andEqualTo(TripRecord::getUserId, baseUserInfoParam.getUserId())).build();

        List<TripRecord> tripRecords = tripRecordService.selectByExample(tripRecordWhere);
        tripRecordService.deleteByExample(tripRecordWhere);
        //2.删健康记录
        List<String> tripIds;
        if (Validator.isNotNullOrEmpty(tripRecords)) {
            tripIds = tripRecords.stream().map(TripRecord::getId).collect(Collectors.toList());
            healthStatusService.deleteByExample(new Example.Builder(HealthStatus.class)
                    .where(WeekendSqls.<HealthStatus>custom().andIn(HealthStatus::getTripRecordId, tripIds)).build());
        }

        //3.删体温记录
        temperatureRecordService.deleteByExample(new Example.Builder(TemperatureRecord.class)
                .where(WeekendSqls.<TemperatureRecord>custom()
                        .andEqualTo(TemperatureRecord::getUserId, baseUserInfoParam.getUserId())
                        .andEqualTo(TemperatureRecord::getFamilyId, baseUserInfoParam.getFamilyId())).build());
        //4.删家庭关系
        housingEstateFamilyUserService.deleteByExample(new Example.Builder(HousingEstateFamilyUser.class)
                .where(WeekendSqls.<HousingEstateFamilyUser>custom()
                        .andEqualTo(HousingEstateFamilyUser::getFamilyId, baseUserInfoParam.getFamilyId())
                        .andEqualTo(HousingEstateFamilyUser::getUserId, baseUserInfoParam.getUserId())).build());
        //5.删小区角色
        baseUserGroupService.deleteByExample(new Example.Builder(BaseUserGroup.class)
                .where(WeekendSqls.<BaseUserGroup>custom()
                        .andEqualTo(BaseUserGroup::getGroupId, Constant.OWNER_ID)
                        .andEqualTo(BaseUserGroup::getAttr1, baseUserInfoParam.getEstateId())
                        .andEqualTo(BaseUserGroup::getUserId, baseUserInfoParam.getUserId())).build());
        //6.删车辆信息
        carService.deleteByExample(new Example.Builder(Car.class)
                .where(WeekendSqls.<Car>custom()
                        .andEqualTo(Car::getEstateId, baseUserInfoParam.getEstateId())
                        .andEqualTo(Car::getUserId, baseUserInfoParam.getUserId())).build());

        //7.删用户表
        BaseUser baseUser = baseUserService.selectById(baseUserInfoParam.getUserId());
        if (Validator.isNullOrEmpty(baseUser.getOpenId())) {
            baseUserService.deleteById(baseUser.getId());
        }
    }


    public BaseResponse checkIsDelete(BaseUserInfoParam baseUserInfoParam) {
        HousingEstateFamilyUser housingEstateFamilyUser = new HousingEstateFamilyUser();
        housingEstateFamilyUser.setUserId(baseUserInfoParam.getUserId());
        housingEstateFamilyUser.setFamilyId(baseUserInfoParam.getFamilyId());
        HousingEstateFamilyUser db = housingEstateFamilyUserService.selectOne(housingEstateFamilyUser);

        if (db == null) {
            return new ObjectRestResponse(ExceptionEnum.DELETE_SUC);
        }else {
            return BaseResponse.success();
        }
    }

    /**
     * 获取住户个人信息
     * @param baseUserInfoParam 小区范围参数
     */
    public BaseResponse getBaseInfo(BaseUserInfoParam baseUserInfoParam) {
        UserBaseInfoVo baseUserInfo = baseUserService.findBaseUserInfo(baseUserInfoParam);
        return new ObjectRestResponse().data(baseUserInfo);
    }

    /**
     * 更新住户信息
     * @param userBaseInfoVo 住户基本信息
     */
    @Transactional(readOnly = false)
    public BaseResponse updateBaseInfo(UserBaseInfoVo userBaseInfoVo) {

        if (Validator.isNotNullOrEmpty(userBaseInfoVo.getMobilePhone())) {
            boolean isPass = smsService.verifySmsCode(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone(), userBaseInfoVo.getVerifyCode());

            if (!isPass) {
                return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, userBaseInfoVo);
            }
        }else {
            userBaseInfoVo.setMobilePhone(null);
        }

        BaseUser baseUser = userBaseInfoVo.buildUser();
        baseUser.setOpenId(null);
        baseUserService.updateSelectiveById(baseUser);

        List<Car> cars = userBaseInfoVo.buildCars(baseUser.getId());
        carService.addCars(cars);
        return new ObjectRestResponse();
    }


    /**
     * 生成通行证(添加住户)
     * @param userBaseInfoVo 住户个人信息
     */
    @Transactional(readOnly = false)
    public BaseResponse addBaseInfo(UserBaseInfoVo userBaseInfoVo) {

        boolean isPass = smsService.verifySmsCode(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone(), userBaseInfoVo.getVerifyCode());

        if (!isPass) {
          return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, userBaseInfoVo);
        }

        //构建业务对象
        BaseUser baseUser = userBaseInfoVo.buildUser();
        HousingEstateFamily housingEstateFamily = userBaseInfoVo.buildFamily();

        //1.插入房屋数据
        BaseUser dbBaseUser = baseUserService.findUserByOpenid(baseUser.getOpenId());
        boolean dbBaseUserIsNull = Validator.isNullOrEmpty(dbBaseUser);
        String userId = dbBaseUserIsNull ? IdGen.uuid() : dbBaseUser.getId();

        HousingEstateFamily dbHousingEstateFamily = housingEstateFamilyService.findFamilyMembers(housingEstateFamily);

        String familyId;
        //boolean userIsNull = true;
        if (Validator.isNullOrEmpty(dbHousingEstateFamily)) {
            housingEstateFamily.setMainUserId(userId);//设置主家庭人员(默认第一个创建者)
            housingEstateFamilyService.insertSelective(housingEstateFamily);
            familyId = housingEstateFamily.getId();
        }else {
            familyId = dbHousingEstateFamily.getId();
            for (BaseUser familyMember : dbHousingEstateFamily.getFamilyMembers()) {
                if (baseUser.getName().equals(familyMember.getName())) {
                    return new ObjectRestResponse(ExceptionEnum.ADD_REPEAT_ERR)
                            .setMessage("该房号下此姓名已存在,请联系户主删除该用户后再注册!");
                }
            }
        }

        //插入
        if (dbBaseUserIsNull) {
            baseUser.setId(userId);
            baseUserService.insertSelective(baseUser);
        }else {
            //更新
            /*if (dbBaseUserIsNull) {
                dbBaseUser = new BaseUser();
            }*/
            //dbBaseUser.setId(userId);
            dbBaseUser.setName(baseUser.getName());
            dbBaseUser.setMobilePhone(baseUser.getMobilePhone());
            dbBaseUser.setOpenId(baseUser.getOpenId());
            dbBaseUser.setIdNumber(baseUser.getIdNumber());
            baseUserService.updateSelectiveById(dbBaseUser);
        }

        //3.插入房屋与业主关系
        housingEstateFamilyUserService.addFamilyUser(familyId, userId);

        //4.插入用户角色
        baseUserGroupService.addUserGroup(Constant.OWNER_ID, userId, housingEstateFamily.getEstateId());
        userBaseInfoVo.setId(userId);
        userBaseInfoVo.setFamilyId(familyId);

        //5.插入车辆信息
        List<Car> cars = userBaseInfoVo.buildCars(userId);
        carService.addCars(cars);

        cacheManagerService.remove(CacheName.SMS, getRegistType(baseUser.getOpenId(), baseUser.getMobilePhone()));

        return new ObjectRestResponse().data(userBaseInfoVo);
    }

    /**
     * 添加家庭成员
     * @param userBaseInfoVo 住户个人信息
     */
    @Transactional(readOnly = false)
    public BaseResponse addMember(UserBaseInfoVo userBaseInfoVo) {

        if (Validator.isNotNullOrEmpty(userBaseInfoVo.getMobilePhone())) {
            boolean isPass = smsService.verifySmsCode(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone(), userBaseInfoVo.getVerifyCode());

            if (!isPass) {
                return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, userBaseInfoVo);
            }
        }

        //1.插入用户
        BaseUser baseUser = userBaseInfoVo.buildUser();
        HousingEstateFamily housingEstateFamily = userBaseInfoVo.buildFamily();
        baseUser.setOpenId(null);

        //校验是否有重名
        HousingEstateFamily dbHousingEstateFamily = housingEstateFamilyService.findFamilyMembers(housingEstateFamily);

        for (BaseUser familyMember : dbHousingEstateFamily.getFamilyMembers()) {
            if (baseUser.getName().equals(familyMember.getName())) {
                return new ObjectRestResponse(ExceptionEnum.ADD_REPEAT_ERR);
            }
        }

        //插入
        if (Validator.isNullOrEmpty(baseUser.getMobilePhone())) {
            BaseUser mainUser = baseUserService.findFamilyMainUser(userBaseInfoVo.getFamilyId()).orElse(new BaseUser());
            baseUser.setMobilePhone(mainUser.getMobilePhone());
        }

        baseUserService.insertSelective(baseUser);

        //3.插入房屋与业主关系
        housingEstateFamilyUserService.addFamilyUser(userBaseInfoVo.getFamilyId(), baseUser.getId());

        //4.插入用户角色
        baseUserGroupService.addUserGroup(Constant.OWNER_ID, baseUser.getId(), userBaseInfoVo.getHousingEstateId());

        //5.添加车辆信息
        List<Car> cars = userBaseInfoVo.buildCars(baseUser.getId());
        carService.addCars(cars);

        userBaseInfoVo.setId(baseUser.getId());
        userBaseInfoVo.setFamilyId(userBaseInfoVo.getFamilyId());

        return new ObjectRestResponse().data(userBaseInfoVo);
    }




    public HousingEstateFamily familyMemberInfo( String familyId, String userId) {
        HousingEstateFamily housingEstateFamily = housingEstateFamilyService.selectById(familyId);

        if (Validator.isNullOrEmpty(housingEstateFamily)) {
            return null;
        }

        HousingEstate housingEstate = housingEstateService.selectById(housingEstateFamily.getEstateId());
        housingEstateFamily.setEstateName(housingEstate.getName());


        List<BaseUser> baseUsers = baseUserService.findFamilyMembers(familyId);
        housingEstateFamily.setFamilyMembers(baseUsers);

        //更新最后一次登录的小区
        HousingEstateFamilyUser housingEstateFamilyUser = new HousingEstateFamilyUser();
        housingEstateFamilyUser.setFamilyId(familyId);
        housingEstateFamilyUser.setUserId(userId);
        Example example = new Example(HousingEstateFamilyUser.class);
        example.createCriteria().andCondition("family_id=", familyId)
                .andCondition("user_id=", userId);
        housingEstateFamilyUserService.updateByExampleSelective(housingEstateFamilyUser, example);

        return housingEstateFamily;
    }

    /**
     * 获取本小区的楼栋信息列表
     * @param estateId 小区
     */
    public List<String> buildings(String estateId) {
        Example example = new Example(HousingEstateFamily.class);

        example.selectProperties("building").createCriteria().andCondition("estate_id=", estateId);

        List<HousingEstateFamily> housingEstateFamilies = housingEstateFamilyService.selectByExample(example);

        if (Validator.isNullOrEmpty(housingEstateFamilies)) {
            return Collections.emptyList();
        }

        List<String> buildings =
                housingEstateFamilies.stream()
                        .map(HousingEstateFamily::getBuilding)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

        return buildings;
    }


    public FamilyAccessRecordAnalysis familyAccessRecordAnalysis(String familyId, String estateId, String userId) {

        FamilyAccessRecordAnalysis familyAccessRecordAnalysis = new FamilyAccessRecordAnalysis();
        HousingEstateFamily housingEstateFamily = housingEstateFamilyService.selectById(familyId);

        HousingEstate housingEstate = housingEstateService.selectById(housingEstateFamily.getEstateId());

        //获取小区配置的出入参数
        List<DataRule> dataRules = dataRuleService.findDataRuleByEstate(estateId);
        if (Validator.isNullOrEmpty(dataRules)) {
            //设置小区的默认值
            dataRules = DataRule.defaultEstateConfig();
        }

        Map<String, String> type2value = dataRules.stream().collect(Collectors.toMap(DataRule::getType, DataRule::getValue, (k1, k2) -> k1));

        familyAccessRecordAnalysis.setAccessRule(AccessRule.transformAccessRule(dataRules, true));
        if (!estateId.equals(housingEstateFamily.getEstateId())) {
            //非同一个小区的处理
            familyAccessRecordAnalysis.setTripRecords(Collections.emptyList());
            return familyAccessRecordAnalysis;
        }

        //近3天的记录
        familyAccessRecordAnalysis.setTripRecords(tripRecordService.findRecords(familyId,null, 2));

        List<TripRecord> records = tripRecordService.findRecordsByRule(
                                                        type2value.get(Constant.accessType),
                                                        familyId,
                                                        userId,
                                                        Integer.valueOf(type2value.get(Constant.accessDays)));

        int inCount = 0;
        int outCount = 0;
        for (TripRecord record : records) {
            String status = record.getStatus();
            if (AccessStatus.IN.getCode().equals(status)) {
                inCount ++;
            }
            if (AccessStatus.OUT.getCode().equals(status)) {
                outCount ++;
            }
        }

        familyAccessRecordAnalysis.setInCount(inCount);
        familyAccessRecordAnalysis.setOutCount(outCount);
        //近3天的记录
        familyAccessRecordAnalysis.setTripRecords(tripRecordService.findRecords(familyId,null, 2));
        //设置小区门岗控制模式
        familyAccessRecordAnalysis.setGuardControlPattern(housingEstate.getGuardControlPattern());

        return familyAccessRecordAnalysis;
    }


    /**
     * 住户自助录入进出记录
     * @param examineVo 进出记录
     */
    public BaseResponse selfEntryRecord(ExamineVo examineVo) {
        TripRecord tripRecord = examineVo.getTripRecord();

        //区块链记录
        //blockChainRecord(tripRecord);
        tripRecordService.insertSelective(tripRecord);

        //体温为非必填字段，如为空可不纳入体温统计数据
        HealthStatus healthStatus = examineVo.getHealthStatus();
        if (healthStatus != null &&
                healthStatus.getTemperature() != null &&
                BigDecimal.ZERO.compareTo(healthStatus.getTemperature()) != 0) {

            healthStatus.setTripRecordId(tripRecord.getId());
            healthStatusService.insertSelective(healthStatus);

            TemperatureRecord temperatureRecord = new TemperatureRecord();
            BeanUtils.copyProperties(tripRecord, temperatureRecord);
            temperatureRecord.setTemperature(healthStatus.getTemperature());
            temperatureRecord.setTimeInterval(DateUtils.checkTimeInterval());
            temperatureRecordService.insertSelective(temperatureRecord);
        }

        return new ObjectRestResponse().data(examineVo);
    }

}
