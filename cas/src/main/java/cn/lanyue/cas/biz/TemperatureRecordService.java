package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.msg.TableResultResponse;
import cn.lanyue.cas.core.utils.MemPageHelper;
import cn.lanyue.cas.core.utils.Query;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.entity.TemperatureRecord;
import cn.lanyue.cas.mapper.TemperatureRecordMapper;
import cn.lanyue.cas.utils.CalculateUtil;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.utils.Distinct;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.response.AbnormalChartsExtVo;
import cn.lanyue.cas.vo.response.AbnormalChartsVo;
import cn.lanyue.cas.vo.response.EstateUserVo;
import cn.lanyue.cas.vo.response.RateChartsVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 体温登记Service
 */
@Service
@Transactional(readOnly = false)
public class TemperatureRecordService extends BaseBiz<TemperatureRecordMapper, TemperatureRecord> {


    @Autowired
    private HousingEstateService housingEstateService;

    /**
     * 分页查询数据
     * @param familyId 家庭id
     * @param query 分页查询参数
     * @return 分页后数据
     */
    public BaseResponse findTemperatureRecordsByfamily(String familyId, Query query) {

        Page<Object> objects = PageHelper.startPage(query.getPage(), query.getLimit());

        /*String days = String.valueOf(query.get("days"));

        Integer queryDays;
        if (!StringUtils.isNumeric(days)) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR, "days 只能为整数类型");
        }else {
            queryDays = Integer.valueOf(days);

        }*/
        //固定查询3天的记录
        List<TemperatureRecord> temperatureRecords = mapper.findTemperatureRecordsByfamilyId(familyId, 2);

        if (Validator.isNullOrEmpty(temperatureRecords)) {
            return new TableResultResponse();
        }

        HousingEstate housingEstate = housingEstateService.selectById(query.get("estateId"));

        if (housingEstate != null) {
            for (TemperatureRecord temperatureRecord : temperatureRecords) {
                String status =
                        temperatureRecord.checkStatus(housingEstate.getRiskTemperature(), housingEstate.getAbnormalTemperature());
                temperatureRecord.setStatus(status);
            }
        }
        return new TableResultResponse(objects.getTotal(), temperatureRecords);
    }

    /**
     * 用户录入体温记录
     * @param temperatureRecord 记录
     */
    public void addTemperatureRecord(TemperatureRecord temperatureRecord) {
        temperatureRecord.setTimeInterval(DateUtils.checkTimeInterval());
        insertSelective(temperatureRecord);
    }


    /**
     * 统计体温异常人数统计
     * @param estateId 小区id
     * @param dataScope 数据范围
     * @return 人数
     */
    public Integer checkEstateAbnormalCount(String estateId, String dataScope) {

        HousingEstate housingEstate = housingEstateService.selectById(estateId);

        if (housingEstate == null) {
            throw new RuntimeException();
        }

        List<TemperatureRecord> temperatureRecords = mapper.estateAbnormalCount(estateId, housingEstate.getAbnormalTemperature(), dataScope);

        if (Validator.isNullOrEmpty(temperatureRecords)) {
            return null;
        }

        temperatureRecords = temperatureRecords.stream()
                .filter(t -> Validator.isNotNullOrEmpty(t.getUserId()))
                .filter(Distinct.distinctByKey(TemperatureRecord::getUserId))
                .collect(Collectors.toList());

        return temperatureRecords.size();
    }

    /**
     * 统计异常人数的的图表
     * @param estateId 小区id
     * @param dataScope 数据范围
     * @return
     */
    public BaseResponse estateAbnormalCharts(String estateId, String dataScope) {

        List<TemperatureRecord> temperatureRecords = mapper.estateAbnormalMax(estateId, dataScope);
        AbnormalChartsExtVo abnormalChartsExtVo = new AbnormalChartsExtVo();
        HousingEstate housingEstate = housingEstateService.selectById(estateId);
        if (housingEstate != null) {
            abnormalChartsExtVo.setEstateAbnormalTemperature(housingEstate.getAbnormalTemperature());
            abnormalChartsExtVo.setEstateRiskTemperature(housingEstate.getRiskTemperature());
        }
        if (Validator.isNullOrEmpty(temperatureRecords)) {
            abnormalChartsExtVo.setAbnormalCharts(Collections.emptyList());
            return new ObjectRestResponse().data(abnormalChartsExtVo);
        }

        TreeMap<LocalDate, List<TemperatureRecord>> date2records =
                temperatureRecords.stream()
                        .collect(
                            Collectors.groupingBy(
                                    t -> DateUtils.date2LocalDate(t.getCrtTime()),
                                    TreeMap::new,
                                    Collectors.toList()
                            )
                        );

        List<LocalDate> dates = DateUtils.betweenDays(dataScope);
        dates.forEach(d-> date2records.merge(d, Collections.emptyList(), (k1,k2)->k1));

        List<AbnormalChartsVo> abnormalChartsVos = Lists.newArrayListWithCapacity(date2records.size());
        for (Map.Entry<LocalDate, List<TemperatureRecord>> entry : date2records.entrySet()) {
            AbnormalChartsVo abnormalChartsVo = new AbnormalChartsVo();
            abnormalChartsVo.setDate(entry.getKey());
            int value = 0;
            int riskValue = 0;
            int abnormalValue = 0;
            for (TemperatureRecord temperatureRecord : entry.getValue()) {
                if (temperatureRecord == null) continue;
                BigDecimal temperature = temperatureRecord.getTemperature();
                if (temperature.compareTo(housingEstate.getRiskTemperature()) < 0) {
                    value++;
                    continue;
                }
                if (temperature.compareTo(housingEstate.getRiskTemperature()) >= 0 && temperature.compareTo(housingEstate.getAbnormalTemperature()) < 0) {
                    riskValue++;
                    continue;
                }
                if (temperature.compareTo(housingEstate.getAbnormalTemperature()) >= 0) {
                    abnormalValue++;
                }
            }
            abnormalChartsVo.setRiskValue(riskValue); //风险人数
            abnormalChartsVo.setAbnormalValue(abnormalValue); //异常人数
            abnormalChartsVo.setValue(Long.valueOf(value)); //正常人数
            //abnormalChartsVo.setEstateRiskTemperature(housingEstate.getRiskTemperature());
            //abnormalChartsVo.setEstateAbnormalTemperature(housingEstate.getAbnormalTemperature());
            abnormalChartsVos.add(abnormalChartsVo);
        }

        if (Validator.isNotNullOrEmpty(abnormalChartsVos)) {
            abnormalChartsExtVo.setAbnormalCharts(abnormalChartsVos);
        }else {
            abnormalChartsExtVo.setAbnormalCharts(Collections.emptyList());
        }

        return new ObjectRestResponse().data(abnormalChartsExtVo);
    }


    public BaseResponse estateTemperatureRateCharts(String estateId, String dataScope) {
        Integer estatePeopleNum = housingEstateService.findEstatePeopleNum(estateId);

        if (estatePeopleNum == null) {
            return BaseResponse.success();
        }
        //某人某天分组查询
        List<TemperatureRecord> temperatureRecords = mapper.estateReportCount(estateId, dataScope);

        TreeMap<LocalDate, Long> collect = temperatureRecords.stream().collect(Collectors.groupingBy(t -> DateUtils.date2LocalDate(t.getCrtTime()),TreeMap::new, Collectors.counting()));

        List<LocalDate> dates = DateUtils.betweenDays(dataScope);
        dates.forEach(d -> collect.merge(d, 0L, (k1,k2) -> k1));

        List<RateChartsVo> rateChartsVos = Lists.newArrayListWithCapacity(collect.size());
        BigDecimal total = new BigDecimal(estatePeopleNum);
        for (LocalDate localDate : collect.keySet()) {
            RateChartsVo rateChartsVo = new RateChartsVo();
            rateChartsVo.setDate(localDate);
            rateChartsVo.setRate(CalculateUtil.safeDivide(collect.get(localDate),total));
            rateChartsVos.add(rateChartsVo);
        }

        return new ObjectRestResponse().data(rateChartsVos);
    }


    public List<EstateUserVo> estateTemperatureSearch(String estateId, Date startDate, Date endDate, BigDecimal temperature) {

        List<EstateUserVo> estateUserVos = mapper.estateTemperatureSearch(estateId, startDate, endDate, temperature, null);

        if (Validator.isNullOrEmpty(estateUserVos)) {
            //return Collections.emptyList();
        }

        List<EstateUserVo> estatePeoples = housingEstateService.findEstatePeoples(estateId);
        estatePeoples.add(new EstateUserVo());
        Map<String, EstateUserVo> collect =
                estatePeoples.stream()
                        .filter(e-> e != null && StringUtils.isNotBlank(e.getId()))
                        .filter(Distinct.distinctByKey(EstateUserVo::getId))
                        .collect(Collectors.toMap(EstateUserVo::getId, Function.identity()));
        for (EstateUserVo estateUserVo : estateUserVos) {
            EstateUserVo temp = collect.get(estateUserVo.getId());

            if (temp != null) {
                estateUserVo.setBuilding(temp.getBuilding());
                estateUserVo.setUnit(temp.getUnit());
                estateUserVo.setRoomNumber(temp.getRoomNumber());
                estateUserVo.setUserName(temp.getUserName());
                estateUserVo.setMobilePhone(temp.getMobilePhone());
            }
        }
        return estateUserVos;
    }



    public List<EstateUserVo> estateNoReportTemperatureSearch(String estateId, Date startDate, Date endDate, String timeInterval) {

        List<EstateUserVo> estatePeoples = housingEstateService.findEstatePeoples(estateId);

        List<EstateUserVo> estateUserVos = mapper.estateTemperatureSearch(estateId, startDate, endDate, null, timeInterval);
        if (Validator.isNullOrEmpty(estatePeoples) && Validator.isNullOrEmpty(estateUserVos)) {
            return Collections.emptyList();
        }

        Map<String, String> collect = estateUserVos.stream()
                .flatMap(e -> e.getTemperatureRecords().stream())
                .collect(Collectors.toMap(
                        t -> t.getUserId() + DateUtils.formatDate(t.getCrtTime()) + t.getTimeInterval(),
                        TemperatureRecord::getTimeInterval,
                        (k1, k2) -> k1)
                );


        List<String> days = DateUtils.betweenDays(startDate, endDate);
        for (EstateUserVo estatePeople : estatePeoples) {
            List<TemperatureRecord> temperatureRecords = Lists.newArrayList();
            for (String day : days) {
                Date date = DateUtils.parseDate(day);
                String am = collect.get(estatePeople.getId() + day + DateUtils.AM);
                String pm = collect.get(estatePeople.getId() + day + DateUtils.PM);
                if (DateUtils.AM.equals(timeInterval)) {
                    if (StringUtils.isBlank(am))
                    temperatureRecords.add(new TemperatureRecord(date, DateUtils.AM));
                    continue;
                }
                if (DateUtils.PM.equals(timeInterval)) {
                    if (StringUtils.isBlank(pm))
                    temperatureRecords.add(new TemperatureRecord(date, DateUtils.PM));
                    continue;
                }
                if (StringUtils.isBlank(am))
                    temperatureRecords.add(new TemperatureRecord(date, DateUtils.AM));
                if (StringUtils.isBlank(pm))
                    temperatureRecords.add(new TemperatureRecord(date, DateUtils.PM));
            }
            estatePeople.setTemperatureRecords(temperatureRecords);
        }

        estatePeoples = estatePeoples.stream().filter(p -> Validator.isNotNullOrEmpty(p.getTemperatureRecords())).collect(Collectors.toList());

        return estatePeoples;
    }



}
