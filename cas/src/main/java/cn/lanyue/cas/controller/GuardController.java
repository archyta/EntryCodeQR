package cn.lanyue.cas.controller;


import cn.lanyue.cas.biz.AuthorizationService;
import cn.lanyue.cas.biz.GuardService;
import cn.lanyue.cas.biz.valid.GuardAddGroup;
import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.msg.TableResultResponse;
import cn.lanyue.cas.core.utils.Exceptions;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.core.validation.AddGroup;
import cn.lanyue.cas.entity.TripRecord;
import cn.lanyue.cas.vo.request.ExamineVo;
import cn.lanyue.cas.vo.request.UserBaseInfoVo;
import cn.lanyue.cas.vo.response.GuardInfoVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 门岗端Controller
 */
@RequestMapping("/guard")
@RestController
public class GuardController {

    @Autowired
    private GuardService guardService;


    @Autowired
    private AuthorizationService authorizationService;

    /**
     * 注册保安
     * @param userBaseInfoVo 保安实体对象
     */
    @PostMapping(value = "/baseinfo")
    public BaseResponse addBaseInfo(@RequestBody @Validated({GuardAddGroup.class}) UserBaseInfoVo userBaseInfoVo) {
       return guardService.addBaseInfo(userBaseInfoVo);
    }

    /**
     * 编辑门岗信息
     * @param userBaseInfoVo
     */
    @PutMapping(value = "/baseinfo")
    public BaseResponse editBaseInfo(@RequestBody UserBaseInfoVo userBaseInfoVo) {
        return guardService.editBaseInfo(userBaseInfoVo);
    }

    /**
     * 保安端审核
     * @param examineVo 审核内容
     */
    @PostMapping(value = "/examine")
    public BaseResponse examine(@RequestBody @Validated({AddGroup.class}) ExamineVo examineVo) {
        guardService.examine(examineVo);
        return BaseResponse.success();
    }

    /**
     * 门岗个人信息查询
     * @param estateId 小区id
     * @param userId 用户id
     */
    @GetMapping(value = "/baseinfo")
    public BaseResponse queryBaseInfo(@RequestParam String estateId, @RequestParam String userId) {

        BaseResponse baseResponse = authorizationService.doAuthorization(estateId, userId, Constant.PROPERTY_ID);

        BaseResponse guardAuth = authorizationService.doAuthorization(estateId, userId, Constant.GUARD_ID);
        if (baseResponse.isSuccess() || !guardAuth.isSuccess()) {
            return new ObjectRestResponse(ExceptionEnum.NO_AUTHORITY);
        }

        GuardInfoVo guardInfoVo = guardService.queryBaseInfo(estateId, userId);
        return new ObjectRestResponse().data(guardInfoVo);
    }

    /**
     * 小区进出人数统计
     * @param estateId 小区id
     * @param params 分页参数
     */
    @GetMapping(value = "/record/count/{estateId}")
    public BaseResponse record(@PathVariable String estateId, @RequestParam Map<String, Object> params) {
        TableResultResponse<TripRecord> tripRecordTableResultResponse;
        Query query = new Query(params);
        tripRecordTableResultResponse = guardService.estateRecordCount(estateId, query);
        return tripRecordTableResultResponse;
    }

}
