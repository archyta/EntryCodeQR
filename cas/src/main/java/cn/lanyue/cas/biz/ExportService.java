package cn.lanyue.cas.biz;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.lanyue.cas.entity.TemperatureRecord;
import cn.lanyue.cas.utils.DateUtils;
import cn.lanyue.cas.utils.ExcelExportBaseStyler;
import cn.lanyue.cas.utils.Validator;
import cn.lanyue.cas.vo.excel.AbnormalVo;
import cn.lanyue.cas.vo.excel.NoReportVo;
import cn.lanyue.cas.vo.response.EstateUserVo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/26 22:45
 */
@Service
public class ExportService {

    private static final Map<String, String> timeIntervalMap = ImmutableMap.of(
                                                                "AM","上午",
                                                                "PM","下午"
                                                                );

    @Autowired
    private TemperatureRecordService temperatureRecordService;



    public Workbook assembleNoReportWorkbook(String estateId, Date startDate, Date endDate, String timeInterval) {

        List<EstateUserVo> estateUserVos =
                temperatureRecordService.estateNoReportTemperatureSearch(estateId, startDate, endDate, timeInterval);

        List<NoReportVo> noReportVos = Lists.newArrayList();
        for (EstateUserVo estateUserVo : estateUserVos) {
            String address = StringUtils.join(estateUserVo.getBuilding(), "栋", estateUserVo.getUnit(), "单元", estateUserVo.getRoomNumber());
            List<TemperatureRecord> temperatureRecords = estateUserVo.getTemperatureRecords();
            if (Validator.isNotNullOrEmpty(temperatureRecords)) {
                for (TemperatureRecord temperatureRecord : temperatureRecords) {
                    NoReportVo noReportVo = new NoReportVo();
                    noReportVo.setAddress(address);
                    noReportVo.setMobilePhone(estateUserVo.getMobilePhone());
                    noReportVo.setUserName(estateUserVo.getUserName());
                    noReportVo.setTimeInterval(DateUtils.formatDate(temperatureRecord.getCrtTime())
                            +" "+ timeIntervalMap.get(temperatureRecord.getTimeInterval()));
                    noReportVos.add(noReportVo);
                }
            }
        }

        String title = StringUtils.join(
                                            DateUtils.formatDate(startDate,"yyyy/MM/dd"),
                                            "~",
                                            DateUtils.formatDate(endDate,"yyyy/MM/dd")
                                    );

        //3.设置导出的样式
        ExportParams exportParams = new ExportParams(title + "  未上报体温人员列表", "未上报体温人员");
        exportParams.setStyle(ExcelExportBaseStyler.class);

        //4.组装好导出的workbook
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, NoReportVo.class, noReportVos);

        return workbook;
    }




    public Workbook assembleAbnormalWorkbook(String estateId, Date startDate, Date endDate, BigDecimal temperature) {
        List<EstateUserVo> estateUserVos =
                temperatureRecordService.estateTemperatureSearch(estateId, startDate, endDate, temperature);

        List<AbnormalVo> abnormalVos = Lists.newArrayList();
        for (EstateUserVo estateUserVo : estateUserVos) {
            String address = StringUtils.join(estateUserVo.getBuilding(), "栋", estateUserVo.getUnit(), "单元", estateUserVo.getRoomNumber());
            List<TemperatureRecord> temperatureRecords = estateUserVo.getTemperatureRecords();
            if (Validator.isNotNullOrEmpty(temperatureRecords)) {
                for (TemperatureRecord temperatureRecord : temperatureRecords) {
                    AbnormalVo abnormalVo = new AbnormalVo();
                    abnormalVo.setAddress(address);
                    abnormalVo.setMobilePhone(estateUserVo.getMobilePhone());
                    abnormalVo.setUserName(estateUserVo.getUserName());
                    abnormalVo.setTemperature(temperatureRecord.getTemperature());
                    abnormalVo.setTimeInterval(temperatureRecord.getCrtTime());
                    abnormalVos.add(abnormalVo);
                }
            }
        }

        String title = StringUtils.join(
                         DateUtils.formatDate(startDate,"yyyy/MM/dd"),
                                    "~",
                                    DateUtils.formatDate(endDate,"yyyy/MM/dd")
                            );

        //3.设置导出的样式
        ExportParams exportParams = new ExportParams(title + "  异常体温人员列表", "异常体温人员");
        exportParams.setStyle(ExcelExportBaseStyler.class);

        //4.组装好导出的workbook
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, AbnormalVo.class, abnormalVos);

        return workbook;
    }

}
