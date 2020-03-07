package cn.lanyue.cas.vo.request;

import cn.lanyue.cas.core.validation.AddGroup;
import cn.lanyue.cas.core.validation.UpdateGroup;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.HousingEstate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/18 20:50
 */
@Getter
@Setter
public class HousingEstateCreate  {

    @NotBlank(groups = {UpdateGroup.class})
    private String id;

    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String openId;

    /**
     * 全称
     */
    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String name;

    /**
     * 地址
     */
    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String address;

    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String responsible;

    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String responsibleMobilePhone;

    @NotBlank(groups = {AddGroup.class,UpdateGroup.class})
    private String streetOfficeName;

    @NotNull(groups = {AddGroup.class,UpdateGroup.class})
    private BigDecimal riskTemperature;

    @NotNull(groups = {AddGroup.class,UpdateGroup.class})
    private BigDecimal abnormalTemperature;

    //小区门岗控制模式
    @NotNull(groups = {AddGroup.class,UpdateGroup.class})
    private String guardControlPattern;

    //代理商id
    private String agentId;


    /**
     * 门头照路径
     */
    private String headPhotoPath;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class})
    private String verifyCode;


    public BaseUser buildBaseUser() {
        BaseUser baseUser = new BaseUser();
        baseUser.setOpenId(this.openId);
        baseUser.setName(this.responsible);
        baseUser.setMobilePhone(this.responsibleMobilePhone);
        return baseUser;
    }

    public HousingEstate buildEstate() {
        HousingEstate estate = new HousingEstate();
        estate.setName(this.name);
        estate.setAddress(this.address);
        estate.setHeadPhotoPath(this.headPhotoPath);
        estate.setStreetOfficeName(this.streetOfficeName);
        estate.setAbnormalTemperature(this.abnormalTemperature);
        estate.setRiskTemperature(this.riskTemperature);
        estate.setAgentId(this.agentId);
        estate.setGuardControlPattern(this.guardControlPattern);
        return estate;
    }
}
