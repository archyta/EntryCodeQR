package cn.lanyue.cas.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * 附件导出工具类
 *
 * @author lanyue
 */
@UtilityClass
@Slf4j
public final class AttachmentExportUtil {

    public static final String XLS = ".xls";

    /**
     * 导出
     *
     * @param workbook workbook
     * @param fileName file name,suffix is not required,and it is not recommended to carry a suffix
     * @param response HttpServletResponse
     */
    public static void export(Workbook workbook, String fileName, HttpServletResponse response) {
        try {
            String suffix = XLS;
            if (workbook instanceof HSSFWorkbook) {
                if (fileName.endsWith(suffix)) {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }
                suffix = XLS;
                response.setContentType("application/vnd.ms-excel");
            } else {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            }
            if (!fileName.endsWith(suffix)) {
                fileName += suffix;
            }
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, CharEncoding.UTF_8));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 一般文件导出接口
     *
     * @param path     文件
     * @param fileName 导出后文件名称
     * @param response 响应流
     */
    public static void export(Path path, String fileName, HttpServletResponse response) {
        try {
            response.setCharacterEncoding(CharEncoding.UTF_8);
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, CharEncoding.UTF_8));
            response.getOutputStream().write(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.isNull(path)) {
                return;
            }
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Delete temp file failure");
            }
        }
    }
}