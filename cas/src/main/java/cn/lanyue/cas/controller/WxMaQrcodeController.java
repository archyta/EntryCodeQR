package cn.lanyue.cas.controller;

import cn.lanyue.cas.biz.QrcodeService;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * <pre>
 *  小程序二维码
 * </pre>
 *
 * @author lanyue
 */
@RestController
@RequestMapping("/wx/qrcode/{appid}")
public class WxMaQrcodeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QrcodeService qrcodeService;

    /**
     * 创建二维码
     *
     * @return 二进制流
     */
    @GetMapping("/{scopeType}")
    public ResponseEntity<byte[]> getQrcode(@PathVariable  String appid,
                                  @PathVariable  String scopeType,
                                  @RequestParam  String id) throws Exception{

        if (Validator.isNullOrEmpty(appid) || Validator.isNullOrEmpty(scopeType) || Validator.isNullOrEmpty(id)) {
            logger.info("appid={},scopeType={},id={}",appid,scopeType,id);
            throw new RuntimeException("请求参数错误");
        }
        byte[] qrcodeUrl = qrcodeService.getQrcodeUrl(appid, scopeType, id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok().headers(headers)
                             .contentLength(qrcodeUrl.length)
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .body(qrcodeUrl);
    }


}
