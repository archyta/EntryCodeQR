package cn.lanyue.cas.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.lanyue.cas.biz.BaseUserService;
import cn.lanyue.cas.config.WxMaConfiguration;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序用户接口
 *
 * @author lanyue
 */
@RestController
@RequestMapping("/wx/user/{appid}")
public class WxMaUserController {


    @Autowired
    private BaseUserService baseUserService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 登陆接口
     */
    @GetMapping("/login")
    public BaseResponse login(@PathVariable String appid, String code) {
        if (StringUtils.isBlank(code)) {
            return ObjectRestResponse.fail("empty jscode");
        }

        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            this.logger.info(session.getSessionKey());
            this.logger.info(session.getOpenid());

            return new ObjectRestResponse().data(baseUserService.buildLoginUser(session));
        } catch (WxErrorException e) {
            this.logger.error(e.getMessage(), e);
            return ObjectRestResponse.fail(e.getMessage());
        }
    }

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>

    @GetMapping("/info")
    public String info(@PathVariable String appid, String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        return JsonUtils.toJson(userInfo);
    }*/

    /**
     * <pre>
     * 获取用户绑定手机号信息
     * </pre>

    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        final WxMaService wxService = WxMaConfiguration.getMaService(appid);

        // 用户信息校验
        if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);

        return JsonUtils.toJson(phoneNoInfo);
    }
     */

}
