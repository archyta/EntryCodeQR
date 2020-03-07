package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.cache.CacheManagerService;
import cn.lanyue.cas.core.cache.CacheName;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.utils.SendSmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cn.lanyue.cas.core.cache.CacheType.getRegistType;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/18 9:39
 */
@Service
public class SmsService {

    @Autowired
    CacheManagerService cacheManagerService;

    //@Cacheable(cacheNames = {"SMS"}, key = "'regist'+#openId")
    public BaseResponse sendRegistrationCode(String openId, String phone) {
        try {
            String verifyCode = SendSmsUtils.sendVerifyCode(phone);
            cacheManagerService.put(CacheName.SMS, getRegistType(openId, phone), verifyCode);
            return BaseResponse.success();
        } catch (RuntimeException e) {
            return new ObjectRestResponse(ExceptionEnum.SEND_SMS_ERR, e.getMessage());
        }
    }

    public boolean verifySmsCode(String openId, String mobilePhone, String code) {
        Object object = cacheManagerService.get(CacheName.SMS, getRegistType(openId, mobilePhone));
        if (object==null) {
            return false;
        }
        return object.equals(code);
    }
}
