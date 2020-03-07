package cn.lanyue.cas.controller;

import cn.lanyue.cas.biz.BaseUserService;
import cn.lanyue.cas.biz.SmsService;
import cn.lanyue.cas.core.cache.CacheManagerService;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.TripRecord;
import cn.lanyue.cas.utils.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/17 15:50
 */
//@RestController
//@RequestMapping("/test")
public class TestController {

    /*@Autowired
    BaseUserService baseUserService;

    @Autowired
    SmsService smsService;

    @GetMapping("/cache")
    public BaseResponse get(@RequestParam String phone) {
        smsService.sendRegistrationCode("1234", phone);
        return new ObjectRestResponse();
    }


    @GetMapping("/remove")
    public BaseResponse getC(@RequestParam String phone) {
        smsService.verifySmsCode("1234", phone, "4321");
        return new BaseResponse();
    }*/

    /*public static void main(String[] args) throws Exception{

        TripRecord tripRecord = new TripRecord();
        tripRecord.setDirection("离开");
        String jsonString = "{\"estate\":\"清御园\",\"tel\":\"13612345679\",\"user\":\"王五\",\"direction\":\"离开\",\"estateId\":\"987654321\"}";
        //tripRecord.set
        String WINGCHAIN_URL = "https://explorer.wingchain.cn/api/broadcast_tx_commit";
        String uuid = UUID.randomUUID().toString();
        StringBuffer sb = new StringBuffer(WINGCHAIN_URL);

        String base64encodedString = Base64.getEncoder().encodeToString(jsonString.getBytes(HttpClientUtils.DEFAULT_CHARSET));
        sb.append("?tx=%22").append(uuid).append("=").append(base64encodedString).append("%22");
        //String base64encodedString2 = Base64.getUrlEncoder().encodeToString(sb.toString().getBytes());
        String url = "";
        url = url.concat(sb.toString());

        String string = HttpClientUtils.getString(url, null, null);
        System.out.println(string);
    }*/

}
