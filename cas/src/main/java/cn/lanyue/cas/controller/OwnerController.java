package cn.lanyue.cas.controller;


import cn.lanyue.cas.biz.BaseUserGroupService;
import cn.lanyue.cas.biz.OwnerService;
import cn.lanyue.cas.biz.TemperatureRecordService;
import cn.lanyue.cas.biz.valid.SelfEntryGroup;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.utils.Exceptions;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.core.validation.AddGroup;
import cn.lanyue.cas.entity.TemperatureRecord;
import cn.lanyue.cas.utils.StringUtils;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.request.BaseUserInfoParam;
import cn.lanyue.cas.vo.request.ExamineVo;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 住户端
 */
@RequestMapping("/owner")
@RestController
@Slf4j
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private BaseUserGroupService baseUserGroupService;

    @Autowired
    private TemperatureRecordService temperatureRecordService;

    /**
     * 查询整个家庭的体温记录列表
     * @param familyId 家庭id
     * @param params 分页参数
     * @return 家庭体温记录分页数据
     */
    @GetMapping(value = "/temperature-record/{familyId}")
    public BaseResponse queryTemperatureRecord(@PathVariable("familyId") @NotBlank String familyId, @RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        return temperatureRecordService.findTemperatureRecordsByfamily(familyId, query);
    }

    /**
     * 住户登记体温
     * @param temperatureRecord 体温记录实体
     * @return suc
     */
    @PostMapping(value = "/register/temperature")
    public BaseResponse registerTemperature(@RequestBody @Validated({AddGroup.class}) TemperatureRecord temperatureRecord) {
        temperatureRecordService.addTemperatureRecord(temperatureRecord);
        return BaseResponse.success();
    }

    /**
     * 查询小区管理员信息
     * @param estateId 小区id
     * @return 小区管理员列表
     */
    @GetMapping(value = "/estate/managers")
    public BaseResponse contactEstateManagers(@RequestParam("estateId") String estateId) {
        if (Validator.isNullOrEmpty(estateId)) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR);
        }
        return  new ObjectRestResponse().data(baseUserGroupService.findEstateManagers(estateId));
    }


    /**
     * 生成通行证
     * @param userBaseInfoVo 用户基本数据
     * @return
     */
    @PostMapping(value = "/baseinfo")
    public BaseResponse addBaseInfo(@RequestBody @Validated({AddGroup.class}) UserBaseInfoVo userBaseInfoVo) {
        try {
            return ownerService.addBaseInfo(userBaseInfoVo);
        } catch (Exception e) {
            log.error("生成通行证失败,{}",Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.REGISTER_ERR, Exceptions.getStackTraceAsString(e));
        }
    }

    /**
     * 获取个人基础信息
     */
    @PostMapping(value = "/baseinfo/get")
    public BaseResponse getBaseinfo(@RequestBody @Validated BaseUserInfoParam baseUserInfoParam) {
        return ownerService.getBaseInfo(baseUserInfoParam);
    }

    /**
     * 获取个人基础信息
     */
    @PostMapping(value = "/is-delete")
    public BaseResponse isDelete(@RequestBody @Validated BaseUserInfoParam baseUserInfoParam) {

        return ownerService.checkIsDelete(baseUserInfoParam);
    }

    /**
     * 更新个人基础信息
     * @param userBaseInfoVo 用户基础信息
     */
    @PutMapping(value = "/baseinfo")
    public BaseResponse updateBaseinfo(@RequestBody UserBaseInfoVo userBaseInfoVo) {
        return ownerService.updateBaseInfo(userBaseInfoVo);
    }

    /**
     * 删除住户信息
     * @param baseUserInfoParam
     */
    @DeleteMapping(value = "/baseinfo")
    public BaseResponse delBaseinfo(@RequestBody @Validated BaseUserInfoParam baseUserInfoParam) {
        ownerService.delUser(baseUserInfoParam);
        return BaseResponse.success();
    }

    /**
     * 添加家庭成员
     * @param userBaseInfoVo
     * @return
     */
    @PostMapping(value = "/member")
    public BaseResponse addMember(@RequestBody @Validated({AddGroup.class}) UserBaseInfoVo userBaseInfoVo) {
        if (Validator.isNullOrEmpty(userBaseInfoVo.getFamilyId())) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR,"familyId cannot be empty");
        }
        if (Validator.isNotNullOrEmpty(userBaseInfoVo.getMobilePhone()) && !StringUtils.isMobile(userBaseInfoVo.getMobilePhone())) {
            return new ObjectRestResponse(ExceptionEnum.MOBILE_PHONE_ERR);
        }

        return ownerService.addMember(userBaseInfoVo);
    }

    /**
     * @param familyId 家庭id
     * @param userId 当前用户id
     * @return 家庭成员的列表
     */
    @GetMapping(value = "/family/baseinfo")
    public BaseResponse familyMemberInfo(@RequestParam String familyId,
                                         @RequestParam String userId) {
        return new ObjectRestResponse().data(ownerService.familyMemberInfo(familyId, userId));
    }

    /**
     * @param estateId 小区id
     * @return 某小区的所有楼栋列表
     */
    @GetMapping(value = "/buildings")
    public BaseResponse buildings(@RequestParam String estateId) {
        return new ObjectRestResponse().data(ownerService.buildings(estateId));
    }

    /**
     * @param familyId 家庭id
     * @return  家庭出入记录
     */
    @GetMapping(value = "/family/access-record")
    public BaseResponse accessRecord(@RequestParam String familyId,
                                     @RequestParam String estateId,
                                     @RequestParam String userId) {
        return new ObjectRestResponse().data(ownerService.familyAccessRecordAnalysis(familyId, estateId, userId));
    }


    /**
     * 极简模式下，用户自主扫码通行
     * @param examineVo 审核参数
     */
    @PostMapping(value = "/passable")
    public BaseResponse passable(@RequestBody @Validated({SelfEntryGroup.class}) ExamineVo examineVo) {
        return ownerService.selfEntryRecord(examineVo);
    }


}
