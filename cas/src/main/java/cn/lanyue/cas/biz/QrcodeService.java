package cn.lanyue.cas.biz;


import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.lanyue.cas.config.properties.UploadProperties;
import cn.lanyue.cas.config.WxMaConfiguration;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.entity.StreetOffice;
import cn.lanyue.cas.utils.FileUtils;
import cn.lanyue.cas.utils.Validator;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;

/**
 * 小程序二维码服务
 * @author lanyue
 */
@Service
public class QrcodeService {

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private StreetOfficeService streetOfficeService;

    @Autowired
    private UploadProperties uploadConfig;

    private static final String placeholder = "{scopeType}";

    /**
     * 小区的标识。此标识与前端约定，用于小程序扫码进入住户注册
     */
    public static final String estate = "estate";
    /**
     * 社区(街道)的标识。此标识与前端约定，用于小程序扫码进入保安注册
     */
    public static final String community = "community";

    public final Map<String, String> pageMap = ImmutableMap.of(
            "estate","index",
            "community","securityIndex"
    );


    public byte[] getQrcodeUrl(String appid, String scopeType, String id ) throws Exception{
        HousingEstate housingEstate = Optional.ofNullable(housingEstateService.selectById(id)).orElse(new HousingEstate());
        if (estate.equals(scopeType)) {

            if (Validator.isNullOrEmpty(housingEstate.getMiniAppQrcode())) {
                return estateHandler(appid, scopeType, id);
            }
           return Files.readAllBytes(FileUtils.getPNGPath(uploadConfig.getQrcodeUploadPath(),id + scopeType));
        }

        if (community.equals(scopeType)) {
            if (Validator.isNullOrEmpty(housingEstate.getMiniAppGuardQrcode())) {
                return communityHandler(appid, scopeType, id);
            }
            return Files.readAllBytes(FileUtils.getPNGPath(uploadConfig.getQrcodeUploadPath(),id + scopeType));
        }

        throw new RuntimeException("不能处理scopeType");
    }

    private final byte[] estateHandler(String appid, String scopeType, String id ) {
        byte[] qrcode = createQrcode(appid, scopeType, id);

        HousingEstate housingEstate = new HousingEstate();
        housingEstate.setId(id);
        housingEstate.setMiniAppQrcode(WxMaConfiguration.getQrcodeUrl(appid).replace(placeholder, pageMap.get(scopeType)).concat(id));
        housingEstateService.updateSelectiveById(housingEstate);
        return qrcode;
    }

    private final byte[] communityHandler(String appid, String scopeType, String id ) {
        byte[] qrcode = createQrcode(appid, scopeType, id);

        HousingEstate housingEstate = new HousingEstate();
        housingEstate.setId(id);
        housingEstate.setMiniAppGuardQrcode(WxMaConfiguration.getQrcodeUrl(appid).replace(placeholder, pageMap.get(scopeType)).concat(id));
        housingEstateService.updateSelectiveById(housingEstate);
        return qrcode;
    }

    /**
     * 调用微信的API获取小程序二维码
     * @param appid
     * @param scopeType
     * @param id
     * @return
     */
    private byte[] createQrcode(String appid, String scopeType, String id ) {
        byte[] qrcodeBytes;
        try {
            //调用微信API获取二维码
            final WxMaService wxService = WxMaConfiguration.getMaService(appid);
            String qrcodeUrl = WxMaConfiguration.getQrcodeUrl(appid).replace(placeholder, pageMap.get(scopeType)).concat(id);
            WxMaQrcodeService qrcodeService = wxService.getQrcodeService();
            //创建二维码
            qrcodeService.createQrcodeBytes(qrcodeUrl, 430);
            //获取小程序码
            qrcodeBytes = qrcodeService.createWxaCodeBytes(qrcodeUrl, 1280, false, new WxMaCodeLineColor("0", "0","0"), false);
            //异步保存二维码文件
            Path path = Paths.get(uploadConfig.getQrcodeUploadPath());
            if(!Files.exists(path)){
                Files.createDirectories(path);
            }
            Files.write(FileUtils.getPNGPath(uploadConfig.getQrcodeUploadPath(), id + scopeType), qrcodeBytes, StandardOpenOption.CREATE);
        } catch (Exception e) {
            throw new RuntimeException("获取-生成二维码失败");
        }
        return qrcodeBytes;
    }


}
