package cn.lanyue.cas.biz;

import cn.lanyue.cas.config.properties.UploadProperties;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.utils.FileUtils;
import cn.lanyue.cas.utils.ImageUtil;
import cn.lanyue.cas.utils.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * @author 
 * @Description 海报服务类
 * @Date 2020/2/19 16:24
 */
@Service
public class PosterService {

    private static final String prefix = "poster";

    @Autowired
    private QrcodeService qrcodeService;

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private UploadProperties uploadProperties;


    public String generateGuard(String appid, String id) {
        HousingEstate housingEstate = Optional.ofNullable(housingEstateService.selectById(id)).orElse(new HousingEstate());

        if (Validator.isNullOrEmpty(housingEstate.getMiniAppGuardQrcode())) {
            try {
                //为空生成小区住户的二维码
                qrcodeService.getQrcodeUrl(appid, QrcodeService.community, id);
            } catch (Exception e) {
                throw new RuntimeException("生成住户用小程序码失败");
            }
        }

        String fileName = StringUtils.join(prefix, id ,FileUtils.JPEG);
        if (Validator.isNullOrEmpty(housingEstate.getGuardPoster())){
            //生成海报
            String qrcode = FileUtils.getPNGPath(uploadProperties.getQrcodeUploadPath(),id + QrcodeService.community).toString();
            try {
                /*BufferedImage bgBufImage = new BufferedImage(595, 842, BufferedImage.TYPE_INT_RGB);
                ImageUtil.setBackGroup(bgBufImage, "#3A6EFF");
                ImageUtil.drawImage(bgBufImage,ImageUtil.getRoundImage(qrcode,-1,-1),160,170,300,300, true);
                ImageUtil.drawText(bgBufImage,"保安端注册",0,100,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,50), true);
                ImageUtil.drawText(bgBufImage,housingEstate.getName(),0,520,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,30), true);
                ImageUtil.drawTextNewLine(bgBufImage,
                        "1.保安扫此码注册保安端账号。                                    " +
                                "2.成功注册后，用此工具扫住户通行证，并查验身份，确定是放行还是劝返。",
                        0,570,50,500,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,20),6,bgBufImage.getWidth());

                ImageUtil.saveImage(bgBufImage,uploadProperties.getPosterGuardUploadPath(),fileName);
                */
                String city = findCity(housingEstate.getAddress());

                BufferedImage bgBufImage = new BufferedImage(2479, 3508, BufferedImage.TYPE_INT_RGB);
                ImageUtil.setBackGroup(bgBufImage, "#3A6EFF");
                if (StringUtils.isNotBlank(city)) {
                    ImageUtil.drawText(bgBufImage,city,100,150,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90), false);
                }
                ImageUtil.drawText(bgBufImage,"保安端注册",0,600,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,300), true);
                ImageUtil.drawImage(bgBufImage,ImageUtil.getRoundImage(qrcode,-1,-1,18),160,800,1300,1300, true);
                ImageUtil.drawText(bgBufImage,housingEstate.getName(),0,2300,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,130), true);
                ImageUtil.drawTextNewLine(bgBufImage,
                        "1.保安扫此码注册保安端账号。                          " +
                                "2.成功注册后，用此工具扫住户通行证，并查验身份，确定是放行还是劝返。",
                        0,2600,150,2000,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90),10,bgBufImage.getWidth());

                ImageUtil.saveImage(bgBufImage,uploadProperties.getPosterGuardUploadPath(),fileName);


                HousingEstate h = new HousingEstate();
                h.setId(id);
                h.setGuardPoster(fileName);
                housingEstateService.updateSelectiveById(h);
            } catch (Exception e) {
                throw new RuntimeException("生成海报异常");
            }
        }
        //直接返回文件名
        return fileName;
    }

    private String findCity(String address) {
        String[] addressArr = StringUtils.split(address, " ");
        if (Validator.isNotNullOrEmpty(addressArr) && addressArr.length > 1 && !"全部".equals(addressArr[1]) ) {
            return addressArr[1];
        }
        return null;
    }


    public String generateOwner(String appid, String id) {
        HousingEstate housingEstate = Optional.ofNullable(housingEstateService.selectById(id)).orElse(new HousingEstate());

        if (Validator.isNullOrEmpty(housingEstate.getMiniAppQrcode())) {
            try {
                //为空生成小区住户的二维码
                qrcodeService.getQrcodeUrl(appid, QrcodeService.estate, id);
            } catch (Exception e) {
                throw new RuntimeException("生成住户用小程序码失败");
            }
        }
        String fileName = StringUtils.join(prefix, id ,FileUtils.JPEG);
        if (Validator.isNullOrEmpty(housingEstate.getOwnerPoster())){
            //生成海报
            String qrcode = FileUtils.getPNGPath(uploadProperties.getQrcodeUploadPath(),id + QrcodeService.estate).toString();
            try {
                /*BufferedImage bgBufImage = new BufferedImage(595, 842, BufferedImage.TYPE_INT_RGB);
                ImageUtil.setBackGroup(bgBufImage, "#3A6EFF");
                ImageUtil.drawImage(bgBufImage,ImageUtil.getRoundImage(qrcode,-1,-1),160,170,300,300, true);
                ImageUtil.drawText(bgBufImage,"疫情防控",0,100,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,50), true);
                ImageUtil.drawText(bgBufImage,housingEstate.getName(),0,520,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,30), true);
                ImageUtil.drawTextNewLine(bgBufImage,
                        "为做好疫情防控,避免交叉感染。请所有小区出入人员,主动登记相关信息,领取“临时通行证”。老人和儿童，请监护人员给予辅助登记。若有不明之处,请联系物业。",
                        0,550,50,500,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,20),6,bgBufImage.getWidth());
                ImageUtil.saveImage(bgBufImage, uploadProperties.getPosterOwnerUploadPath(), fileName);*/

                String city = findCity(housingEstate.getAddress());
                BufferedImage bgBufImage = new BufferedImage(2479, 3508, BufferedImage.TYPE_INT_RGB);
                ImageUtil.setBackGroup(bgBufImage, "#3A6EFF");
                if (StringUtils.isNotBlank(city)) {
                    ImageUtil.drawText(bgBufImage,city,100,150,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90), false);
                }
                ImageUtil.drawText(bgBufImage,"疫情防控",0,600,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,300), true);
                ImageUtil.drawImage(bgBufImage,ImageUtil.getRoundImage(qrcode,-1,-1,18),160,800,1300,1300, true);
                ImageUtil.drawText(bgBufImage,housingEstate.getName(),0,2300,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,130), true);

                ImageUtil.drawTextNewLine(bgBufImage,
                        "为做好疫情防控，避免交叉感染。请所有小区出入人员，主动登记相关信息，领取“临时通行证”。老人和儿童，请监护人员给予辅助登记。若有不明之处，请联系物业。",
                        0,2500,150,2000,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90),10,bgBufImage.getWidth());
                ImageUtil.saveImage(bgBufImage, uploadProperties.getPosterOwnerUploadPath(), fileName);

                HousingEstate h = new HousingEstate();
                h.setId(id);
                h.setOwnerPoster(fileName);
                housingEstateService.updateSelectiveById(h);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("生成海报异常");
            }
        }
        return fileName;
    }


}
