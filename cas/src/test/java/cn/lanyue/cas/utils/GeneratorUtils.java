package cn.lanyue.cas.utils;

import cn.lanyue.cas.common.Constants;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class GeneratorUtils {

    static Logger logger = LoggerFactory.getLogger(GeneratorUtils.class);

    public static void output(String ftlName, Map<String, Object> root,
                              String outFile) throws Exception {
        FileWriter out = null;
        try {
            File file = new File(outFile);
            if (file.exists()) {
                if (!file.delete()) {
                   logger.info("删除成功");
                }
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            out = new FileWriter(file);
            Template template = getTemplate(ftlName);
            template.process(root, out); // 模版输出
            logger.info("out file :"+outFile);
            out.flush();
        } catch (TemplateException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        }
    }

    private static Template getTemplate(String ftlName) throws Exception {
        try {
            Configuration cfg = new Configuration();
            cfg.setEncoding(Locale.CHINA, "utf-8");
            cfg.setDirectoryForTemplateLoading(new File("/" + StringUtil
                    .substringAfter(
                            Thread.currentThread().getContextClassLoader().getResource("").toString(), "file:/") + "templates/"));
            Template temp = cfg.getTemplate(ftlName);
            return temp;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String getOutputPath(String fileName) {
        return "e:/"
                + Constants.PROJECT_PATH
                + fileName;
    }

    public static String getTargetFilePath(String pkgPath, String clzName, String suffix) {
        String result = pkgPath.replace(StringUtil.DOT,
                StringUtil.FILE_SEPARATOR);
        result = result + StringUtil.FILE_SEPARATOR;
        result = result + clzName + StringUtil.DOT + suffix;
        if (clzName.endsWith("Test")) {// 单元测试类
            result = Constants.TEST_PATH + result;
        } else if ("xml".equalsIgnoreCase(suffix)) {
            result = Constants.Mapper_PATH + clzName + StringUtil.DOT + suffix;
        } else {
            result = Constants.SRC_PATH + result;// 普通类
        }
        return result;
    }
}
