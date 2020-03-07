package cn.lanyue.cas.controller;

import cn.lanyue.cas.config.properties.UploadProperties;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.core.utils.IdGen;
import cn.lanyue.cas.utils.FileUtils;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * <pre>
 *  小程序临时素材接口
 *  Created by BinaryWang on 2017/6/16.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@RestController
@RequestMapping("/wx/media/{appid}")
public class WxMaMediaController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UploadProperties uploadProperties;

    /**
     * 上传临时素材
     *
     * @return 素材的media_id列表，实际上如果有的话，只会有一个
     */
    @PostMapping("/upload")
    public BaseResponse uploadMedia(@PathVariable String appid, HttpServletRequest request) throws WxErrorException {
        //final WxMaService wxService = WxMaConfiguration.getMaService(appid);


        CommonsMultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());

        if (!resolver.isMultipart(request)) {
            return new ObjectRestResponse(ExceptionEnum.UPLOAD_FILE_FAILED);
        }

        try {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator<String> it = multiRequest.getFileNames();
            String fileName = null;
            while (it.hasNext()) {
                try {
                    MultipartFile file = multiRequest.getFile(it.next());
                    if (!FileUtils.checkSuffix(file.getOriginalFilename())) {
                        return new ObjectRestResponse(ExceptionEnum.UPLOAD_FILE_FAILED);
                    }
                    fileName = StringUtils.join(IdGen.uuid(), FileUtils.getImgSuffix(file.getOriginalFilename()));
                    Path path = Paths.get(uploadProperties.getHeadPhotoUploadPath());
                    if(!java.nio.file.Files.exists(path)){
                        Files.createDirectories(path);
                    }
                    File newFile = new File(uploadProperties.getHeadPhotoUploadPath(), fileName);
                    this.logger.info("filePath is ：" + newFile.toString());
                    file.transferTo(newFile);
                    //WxMediaUploadResult uploadResult = wxService.getMediaService().uploadMedia(WxMaConstants.KefuMsgType.IMAGE, newFile);
                    //this.logger.info("media_id ： " + uploadResult.getMediaId());
                    //result.add(uploadResult.getMediaId());
                } catch (IOException e) {
                    this.logger.error(e.getMessage(), e);
                }
            }

            return new ObjectRestResponse().data(fileName);
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.UPLOAD_FILE_FAILED, e.getMessage());
        }
    }

    /**
     * 下载临时素材
     */
   /* @GetMapping("/download/{mediaId}")
    public File getMedia(@PathVariable String appid, @PathVariable String mediaId) throws WxErrorException {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        return wxService.getMediaService().getMedia(mediaId);
    }*/
}
