package cn.lanyue.cas.biz;


import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.cache.CacheManagerService;
import cn.lanyue.cas.core.cache.CacheName;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.msg.TableResultResponse;
import cn.lanyue.cas.core.utils.Exceptions;
import cn.lanyue.cas.core.utils.IdGen;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.entity.*;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.utils.HttpClientUtils;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.request.ExamineVo;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import cn.lanyue.cas.vo.response.GuardInfoVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static cn.lanyue.cas.core.cache.CacheType.getRegistType;


/**
 * 门岗端
 */
@Service
@Slf4j
public class GuardService {

    /**
     * 区块链API
     * 用于门岗审核通过后记录到区块链
     * @see cn.lanyue.cas.biz.GuardService#examine(ExamineVo)
     */
    @Value("${cas.blockchain.wingChainUrl}")
    private String wingChainUrl;


    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private HousingEstateGuardService  housingEstateGuardService;

    @Autowired
    private TripRecordService  tripRecordService;

    @Autowired
    private HealthStatusService healthStatusService;

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private BaseUserGroupService baseUserGroupService;

//    @Autowired
//    private StreetOfficeService streetOfficeService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TemperatureRecordService temperatureRecordService;

    @Autowired
    CacheManagerService cacheManagerService;

    @Transactional(readOnly = false)
    public BaseResponse addBaseInfo(UserBaseInfoVo userBaseInfoVo) {

        boolean isPass = smsService.verifySmsCode(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone(), userBaseInfoVo.getVerifyCode());

        if (!isPass) {
            return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, userBaseInfoVo);
        }

        //1.查找用户表
        BaseUser dbUser = baseUserService.findUserByOpenid(userBaseInfoVo.getOpenId());
        /*if (Validator.isNullOrEmpty(dbUser)) {
            dbUser = baseUserService.findUserByNameIdNum(userBaseInfoVo.getName(), userBaseInfoVo.getIdNumber());
        }*/

        BaseUser baseUser ;
        if (dbUser ==null) {
            baseUser = userBaseInfoVo.buildUser();
            //baseUser.setOpenId(null);
            baseUserService.insertSelective(baseUser);
        }else {
            dbUser.setName(userBaseInfoVo.getName());
            dbUser.setMobilePhone(userBaseInfoVo.getMobilePhone());
            baseUserService.updateSelectiveById(dbUser);
            baseUser = dbUser;
        }
        //保存用户组
        baseUserGroupService.addUserGroup(Constant.GUARD_ID, baseUser.getId(), userBaseInfoVo.getHousingEstateId());
        //保存小区保安信息
        housingEstateGuardService.addGuard(userBaseInfoVo.getHousingEstateId(), baseUser.getId());
        //删除验证码缓存
        cacheManagerService.remove(CacheName.SMS, getRegistType(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone()));
        userBaseInfoVo.setId(baseUser.getId());
        return new ObjectRestResponse().data(userBaseInfoVo);
    }

    @Transactional(readOnly = false)
    public BaseResponse editBaseInfo(UserBaseInfoVo userBaseInfoVo) {
        boolean isPass = smsService.verifySmsCode(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone(), userBaseInfoVo.getVerifyCode());

        if (!isPass) {
            return new ObjectRestResponse(ExceptionEnum.VERIFY_SMS_ERR, userBaseInfoVo);
        }
        BaseUser baseUser = userBaseInfoVo.buildUser();
        baseUser.setId(userBaseInfoVo.getId());
        baseUserService.updateSelectiveById(baseUser);
        cacheManagerService.remove(CacheName.SMS, getRegistType(userBaseInfoVo.getOpenId(), userBaseInfoVo.getMobilePhone()));
        return BaseResponse.success();
    }


    @Transactional(readOnly = false)
    public void examine(ExamineVo examineVo) {
        TripRecord tripRecord = examineVo.getTripRecord();

        //区块链记录
        //blockChainRecord(tripRecord);

        tripRecordService.insertSelective(tripRecord);

        HealthStatus healthStatus = examineVo.getHealthStatus();
        healthStatus.setTripRecordId(tripRecord.getId());

        //体温为非必填字段，如为空可不纳入体温统计数据
        if (Validator.isNotNullOrEmpty(healthStatus.getTemperature())) {
            TemperatureRecord temperatureRecord = new TemperatureRecord();
            BeanUtils.copyProperties(tripRecord, temperatureRecord);
            temperatureRecord.setTemperature(healthStatus.getTemperature());
            temperatureRecord.setTimeInterval(DateUtils.checkTimeInterval());
            temperatureRecordService.insertSelective(temperatureRecord);
        }

        healthStatusService.insertSelective(healthStatus);
    }


    /**
     * 记录区块链
     * @param tripRecord 出行记录
     */
    private void blockChainRecord(TripRecord tripRecord) {

        BaseUser baseUser = baseUserService.selectById(tripRecord.getUserId());
        if (baseUser != null)  {
            tripRecord.setUserName(baseUser.getName());
            tripRecord.setMobilePhone(baseUser.getMobilePhone());
        }

        HousingEstate housingEstate = housingEstateService.selectById(tripRecord.getEstateId());
        if (housingEstate != null) tripRecord.setEstateName(housingEstate.getName());

        Gson gson = new Gson();
        String tripRecordJson = gson.toJson(tripRecord);
        try {
            String base64encodedString =
                    Base64.getEncoder().encodeToString(tripRecordJson.getBytes(HttpClientUtils.DEFAULT_CHARSET));

            String url = StringUtils.join(
                    wingChainUrl,
                             "?tx=%22",
                             IdGen.uuid(),
                             "=",
                             base64encodedString,
                             "%22"
                        );

            String response = HttpClientUtils.getString(url, null, null);
            if (response != null) {
                Map m = gson.fromJson(response, Map.class);
                if(m!=null & m.get("result") != null) {
                    Map m2 = (Map) m.get("result");
                    tripRecord.setHash(m2.get("hash").toString());
                    tripRecord.setHeight(m2.get("height").toString());
                }
            }
        } catch (Exception e) {
            log.error("记录 {} 区块链失败,原因：{}", tripRecordJson, Exceptions.getStackTraceAsString(e));
        }
    }


    /**
     * 模型支撑一个门卫在多个小区任职，目前暂时只支持一个保安一个小区
     * @param estateId 小区id
     * @param userId 用户id
     */
    public GuardInfoVo queryBaseInfo(String estateId, String userId) {

        return new GuardInfoVo(
                        housingEstateService.selectById(estateId),
                        baseUserService.selectById(userId)
                    );
    }

    /**
     * 小区出入记录
     * @param estateId 小区id
     * @param query 分页参数
     */
    public TableResultResponse<TripRecord> estateRecordCount(String estateId, Query query) {

        Page<Object> objects = PageHelper.startPage(query.getPage(), query.getLimit());
        List<TripRecord> records = tripRecordService.findRecords(null, estateId, 0);

        if (Validator.isNullOrEmpty(records)) {
            return new TableResultResponse();
        }

        return new TableResultResponse(objects.getTotal(), records);
    }
}
