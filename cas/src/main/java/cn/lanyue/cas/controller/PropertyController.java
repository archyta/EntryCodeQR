package cn.lanyue.cas.controller;


import cn.lanyue.cas.biz.AuthorizationService;
import cn.lanyue.cas.biz.PropertyService;
import cn.lanyue.cas.biz.TemperatureRecordService;
import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.msg.TableResultResponse;
import cn.lanyue.cas.core.utils.Exceptions;
import cn.lanyue.cas.core.utils.MemPageHelper;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.core.validation.AddGroup;
import cn.lanyue.cas.core.validation.UpdateGroup;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.vo.request.AccessRule;
import cn.lanyue.cas.vo.request.HousingEstateCreate;
import cn.lanyue.cas.vo.request.ManagerVo;
import cn.lanyue.cas.vo.response.EstateUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门卫端控制器
 */
@RequestMapping("/property")
@RestController
@Slf4j
public class PropertyController {

    @Autowired
    private PropertyService propertyService;


    @Autowired
    private TemperatureRecordService temperatureRecordService;

    @Autowired
    private AuthorizationService authorizationService;


    /**
     * 异常体温情况高级搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param temperature 体温
     * @return
     */
    @GetMapping(value = "/temperature/search")
    public BaseResponse temperatureSearch(@RequestParam String estateId,
                                          @RequestParam Date startDate,
                                          @RequestParam Date endDate,
                                          @RequestParam String temperature,
                                          @RequestParam(required = false) String page,
                                          @RequestParam(required = false) String limit) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("limit", limit);
            Query query = new Query(params);

            BigDecimal temp;
            if (StringUtils.isNotBlank(temperature)) {
                temp = new BigDecimal(temperature);
            }else {
                temp = null;
            }
            List<EstateUserVo> estateUserVos = temperatureRecordService.estateTemperatureSearch(estateId, startDate, endDate, temp);
            TableResultResponse<EstateUserVo> tableResultResponse = MemPageHelper.start(estateUserVos, query);
            return tableResultResponse;
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    /**
     * 未登记体温高级查询
     * @param estateId 小区id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param timeInterval 区间
     * @param page 页码
     * @param limit 每页条数
     * @return 分页数据
     */
    @GetMapping(value = "/noreport/temperature/search")
    public BaseResponse noreportTemperatureSearch(@RequestParam String estateId,
                                          @RequestParam Date startDate,
                                          @RequestParam Date endDate,
                                          @RequestParam String timeInterval,
                                          @RequestParam(required = false) String page,
                                          @RequestParam(required = false) String limit) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("limit", limit);
            Query query = new Query(params);

            if (!DateUtils.PM.equals(timeInterval) && !DateUtils.AM.equals(timeInterval)) {
                timeInterval = null;
            }

            List<EstateUserVo> estateUserVos = temperatureRecordService.estateNoReportTemperatureSearch(estateId, startDate, endDate, timeInterval);
            TableResultResponse<EstateUserVo> tableResultResponse = MemPageHelper.start(estateUserVos, query);

            return tableResultResponse;
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    /**
     * 上报体温的比例图表
     * @param estateId 小区id
     * @param dataScope 数据范围
     */
    @GetMapping(value = "/temperature/rate")
    public BaseResponse registerTemperatureRate(@RequestParam String estateId, @RequestParam String dataScope) {
        try {
            return temperatureRecordService.estateTemperatureRateCharts(estateId, dataScope);
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }


    /**
     * 体温异常人员统计图
     * @param estateId 小区id
     * @param dataScope 数据范围
     * @return 统计图
     */
    @GetMapping(value = "/abnormal/charts")
    public BaseResponse abnormalCount(@RequestParam String estateId, @RequestParam String dataScope) {
        try {
            return temperatureRecordService.estateAbnormalCharts(estateId, dataScope);
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    /**
     * 添加管理者
     * @param managerVo 管理者实体
     */
    @PostMapping(value = "/manager")
    public BaseResponse addManager(@RequestBody ManagerVo managerVo) {
        try {
            return propertyService.addManager(managerVo);
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    /**
     * 删除管理员接口
     * @param managerVo 管理人员参数时态
     * @return
     */
    @DeleteMapping(value = "/manager")
    public BaseResponse delManager(@RequestBody @Validated ManagerVo managerVo) {
        try {
            return propertyService.delManager(managerVo);
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    /**
     * 删除人员
     * @param managerVo
     * @return
     */
    @DeleteMapping(value = "/member")
    public BaseResponse delMmember(@RequestBody @Validated ManagerVo managerVo) {
        try {
            propertyService.delManager(managerVo);
            propertyService.delGuard(managerVo);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("{}类,{}",this.getClass().getSimpleName(), Exceptions.getStackTraceAsString(e));
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }


    @PostMapping(value = "/baseinfo")
    public BaseResponse addBaseInfo(@RequestBody @Validated(AddGroup.class) HousingEstateCreate housingEstateCreate) {
        return propertyService.addBaseInfo(housingEstateCreate);
    }

    /**
     * 更新小区信息
     * @param housingEstateCreate
     * @return
     */
    @PutMapping(value = "/baseinfo")
    public BaseResponse update(@RequestBody @Validated(UpdateGroup.class) HousingEstateCreate housingEstateCreate) {
        return propertyService.update(housingEstateCreate);
    }

    /**
     * 更新小区出入配置
     * @param accessRule
     * @return
     */
    @PutMapping(value = "/access-rule")
    public BaseResponse updateAccessRule(@RequestBody @Validated(UpdateGroup.class) AccessRule accessRule) {
        propertyService.updateAccessRule(accessRule);
        return new ObjectRestResponse();
    }

    @GetMapping(value = "/access-rule/{estateId}")
    public BaseResponse getAccessRule(@PathVariable String estateId) {
        return new ObjectRestResponse().data(propertyService.getAccessRules(estateId));
    }

    @GetMapping(value = "/baseinfo/{estateId}")
    public BaseResponse queryBaseInfo(@PathVariable String estateId) {
        try {
            return new ObjectRestResponse().data(propertyService.findOne(estateId));
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED,e.getMessage());
        }
    }

    @DeleteMapping(value = "/guard/{userId}")
    public BaseResponse delGuard(@RequestParam String estateId, @PathVariable String userId) {
        try {
            ManagerVo managerVo = new ManagerVo();
            managerVo.setEstateId(estateId);
            managerVo.setUserId(userId);
            return propertyService.delGuard(managerVo);
            /*Example example = new Example(HousingEstateGuard.class);
            example.createCriteria().andCondition("estate_id=", estateId)
                    .andCondition("user_id=", guardId);
            int i = housingEstateGuardMapper.deleteByExample(example);

            Example example1 = new Example(BaseUserGroup.class);
            example1.createCriteria().andCondition("user_id=", guardId)
                    .andCondition("group_id=", Constant.GUARD_ID)
                    .andCondition("attr1=", estateId);
            baseUserGroupService.deleteByExample(example1);
*/
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED);
        }
    }

    /**
     * 小区人员管理者人员列表(含保安、管理者)
     * @param estateId 小区id
     */
    @GetMapping(value = "/guards/{estateId}")
    public BaseResponse guards(@PathVariable String estateId) {
        try {
            return new ObjectRestResponse().data(propertyService.guards(estateId));
        } catch (Exception e) {
            return ObjectRestResponse.fail(e.getMessage());
        }
    }

    /**
     *
     * @param estateId 小区id
     * @param dataScope month /week /today /all /lastMonth
     * @return
     */
    @GetMapping(value = "/owner/record/total")
    public BaseResponse charts(@RequestParam String estateId,
                               @RequestParam String userId,
                               @RequestParam String dataScope) {
        try {
            BaseResponse baseResponse = authorizationService.doAuthorization(estateId, userId, Constant.PROPERTY_ID);

            if (!baseResponse.isSuccess()) {
                return baseResponse;
            }

            return new ObjectRestResponse().data(propertyService.recordTotal(estateId, dataScope));
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.OPERATION_FAILED);
        }
    }

    @GetMapping(value = "/owner/record/inout")
    public BaseResponse inout(@RequestParam String estateId, @RequestParam String dataScope) {
        try {
            return new ObjectRestResponse().data(propertyService.inout2charts(estateId, dataScope));
        } catch (Exception e) {
            return ObjectRestResponse.fail(e.getMessage());
        }
    }

    @GetMapping(value = "/owner/record/back")
    public BaseResponse back(@RequestParam String estateId, @RequestParam String dataScope) {
        try {
            return new ObjectRestResponse().data(propertyService.back2charts(estateId, dataScope));
        } catch (Exception e) {
            return ObjectRestResponse.fail(e.getMessage());
        }
    }

    /**
     *
     * @param params month /week /today /all /lastMonth
     * @return
     */
    @PostMapping(value = "/owner/record/search")
    public BaseResponse search(@RequestBody Map<String, Object> params) {
        try {
            Query query = new Query(params);
            return propertyService.highSearch(query);
        } catch (Exception e) {
            return ObjectRestResponse.fail(e.getMessage());
        }
    }


}
