package cn.lanyue.cas.controller;

import cn.lanyue.cas.biz.ExportService;
import cn.lanyue.cas.biz.HousingEstateService;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.utils.AttachmentExportUtil;
import cn.lanyue.cas.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/26 22:41
 */
@RequestMapping("/export")
@RestController
@Slf4j
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private HousingEstateService housingEstateService;

    /**
     * 未上报人员导出
     * @param estateId 小区id
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param timeInterval
     * @param response
     */
    @GetMapping("/no-report")
    public void export(@RequestParam String estateId,
                       @RequestParam Date startDate,
                       @RequestParam Date endDate,
                       @RequestParam String timeInterval,
                       HttpServletResponse response) {

        if (!DateUtils.PM.equals(timeInterval) && !DateUtils.AM.equals(timeInterval)) {
            timeInterval = null;
        }

        Workbook workbook = exportService.assembleNoReportWorkbook(estateId, startDate, endDate, timeInterval);

        HousingEstate housingEstate = housingEstateService.selectById(estateId);

        AttachmentExportUtil.export(workbook, housingEstate.getName()+"未上报体温人员列表" ,response);
    }


    /**
     * 未上报人员导出
     * @param estateId 小区id
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param timeInterval
     * @param response
     */
    @GetMapping("/abnormal/temperature")
    public void abnormalTemperature(@RequestParam String estateId,
                       @RequestParam Date startDate,
                       @RequestParam Date endDate,
                       @RequestParam String temperature,
                       HttpServletResponse response) {

        BigDecimal temp;
        if (StringUtils.isNotBlank(temperature)) {
            temp = new BigDecimal(temperature);
        }else {
            temp = null;
        }

        Workbook workbook = exportService.assembleAbnormalWorkbook(estateId, startDate, endDate, temp);

        HousingEstate housingEstate = housingEstateService.selectById(estateId);

        AttachmentExportUtil.export(workbook, housingEstate.getName() + "异常体温人员列表" ,response);

    }


}
