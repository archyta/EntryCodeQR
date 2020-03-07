package cn.lanyue.cas.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class FileUtils {

    public static final String PNG = ".png";
    public static final String JPEG = ".jpeg";

    private static final  String[] FILETYPES = new String[]{
            ".jpg", ".bmp", ".jpeg", ".png", ".gif",
            ".JPG", ".BMP", ".JPEG", ".PNG", ".GIF"
    };

    public Path getPath(String savePath, String fileName, String suffix) {
        return Paths.get(savePath + fileName + suffix);
    }

    public Path getPNGPath(String savePath, String fileName) {
        return Paths.get(savePath + fileName + PNG);
    }

    public Path getJPEGPath(String savePath, String fileName) {
        return Paths.get(savePath + fileName + JPEG);
    }


    public boolean checkSuffix(String imgFileName) {
        Boolean flag =false;
        //图片格式
        if(!StringUtils.isBlank(imgFileName)){
           String suffix = getImgSuffix(imgFileName);
            for (int i = 0; i < FILETYPES.length; i++) {
                String fileType = FILETYPES[i];
                if (suffix.equals(fileType)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public String getImgSuffix(String imgFileName) {
        //图片格式
        if(!StringUtils.isBlank(imgFileName)){
            for (int i = 0; i < FILETYPES.length; i++) {
                String fileType = FILETYPES[i];
                if (imgFileName.endsWith(fileType)) {
                    return fileType;
                }
            }
        }
        return null;
    }


}
