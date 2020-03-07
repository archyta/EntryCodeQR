package cn.lanyue.cas.controller;


import cn.lanyue.cas.biz.AgentService;
import cn.lanyue.cas.biz.HousingEstateService;
import cn.lanyue.cas.biz.SmsService;
import cn.lanyue.cas.biz.StreetOfficeService;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.entity.Agent;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.entity.StreetOffice;
import cn.lanyue.cas.utils.StringUtils;
import cn.lanyue.cas.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import sun.management.resources.agent;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${cas.contact-phone}")
    private String phone;

    @Value("${cas.contact-name}")
    private String name;

    @Autowired
    private HousingEstateService housingEstateService;

    @Autowired
    private StreetOfficeService streetOfficeService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AgentService agentService;

    @GetMapping("/estate/info/{estateId}")
    public BaseResponse getEstateInfo(@PathVariable String estateId) {
        HousingEstate housingEstate = housingEstateService.selectById(estateId);
        if (Validator.isNullOrEmpty(housingEstate)) {
            return BaseResponse.success("找不到该小区信息");
        }
        //StreetOffice streetOffice = streetOfficeService.selectById(housingEstate.getStreetOfficeId());
        //housingEstate.setStreetOfficeName(streetOffice.getName());
        return new ObjectRestResponse().data(housingEstate);
    }


    @GetMapping("/contact/phone")
    public BaseResponse contactUs(@RequestParam(required = false) String estateId,
                                  @RequestParam(required = false) String agentId) {
        BaseUser baseUser = new BaseUser();
        baseUser.setName(name);
        baseUser.setMobilePhone(phone);

        if (Validator.isNullOrEmpty(estateId) && Validator.isNullOrEmpty(agentId)) {
            return new ObjectRestResponse().data(baseUser);
        }

        //查找小区对应的代理商
        HousingEstate housingEstate = housingEstateService.selectById(estateId);
        if (Validator.isNotNullOrEmpty(housingEstate) && Validator.isNotNullOrEmpty(housingEstate.getAgentId())) {
            Agent agent = agentService.selectById(housingEstate.getAgentId());
            if (agent != null) {
                baseUser.setName(agent.getName());
                baseUser.setMobilePhone(agent.getMobilePhone());
            }
            return new ObjectRestResponse().data(baseUser);
        }

        //查询代理商
        Agent agent = agentService.selectById(agentId);
        if (agent != null) {
            baseUser.setName(agent.getName());
            baseUser.setMobilePhone(agent.getMobilePhone());
        }

        return new ObjectRestResponse().data(baseUser);
    }

    /**
     * 发送验证码
     * @param openId
     * @param phone
     * @return
     */
    @GetMapping("/sms/verify-code")
    public BaseResponse sendVerifyCode(@RequestParam String openId, @RequestParam String phone) {
        if (!StringUtils.isMobile(phone)) {
            return new ObjectRestResponse(ExceptionEnum.PARAM_ERR).setMessage("手机号码格式错误");
        }

        return smsService.sendRegistrationCode(openId, phone);
    }
}
