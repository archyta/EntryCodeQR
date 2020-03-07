package cn.lanyue.cas.utils;

import cn.lanyue.cas.core.utils.Exceptions;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 
 * @Description 阿里云短信服务
 * @Date 2020/2/16 16:31
 */
@UtilityClass
@Slf4j
public class SendSmsUtils {

    //阿里云acs配置
    private static final DefaultProfile profile = DefaultProfile.getProfile(
                                        "地域",
                                        "短信签名",
                                        "短信模板");
    //acs客户端
    private static final IAcsClient client = new DefaultAcsClient(profile);

    private static final String OK = "OK";

    private static final Gson gson = new Gson();

    /**
     * 发送短信验证码
     * @param phoneNumber 发送的电话号码
     * @return 验证码
     */
    public String sendVerifyCode(String phoneNumber) {
        try {
            String verifyCode = randomNum();
            CommonRequest request = getSmsRequest(phoneNumber, verifyCode);
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            log.info("电话{}发送验证码{}", phoneNumber, verifyCode);

            Map map = gson.fromJson(data, Map.class);
            if (!OK.equals(map.get("Code"))) {
                log.error("电话{}发送验证码失败,原因：{}", phoneNumber, data);
                throw new RuntimeException(data);
            }
            return verifyCode;
        } catch (ServerException e) {
            log.error(Exceptions.getStackTraceAsString(e));
        } catch (ClientException e) {
            log.error(Exceptions.getStackTraceAsString(e));
        }
        throw new RuntimeException("发送验证码异常");
    }

    private CommonRequest getSmsRequest(String phoneNumber, String verifyCode) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "区域");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", "签名");
        request.putQueryParameter("TemplateCode", "模板ID");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+ verifyCode +"\"}");
        return request;
    }


    private String randomNum() {
        return String.valueOf((int)((Math.random()*9+1)*1000));
    }

}
