package cn.lanyue.cas.vo.request;


import cn.lanyue.cas.biz.valid.GuardAddGroup;
import cn.lanyue.cas.core.entity.BaseEntity;
import cn.lanyue.cas.core.validation.AddGroup;
import cn.lanyue.cas.core.validation.UpdateGroup;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.Car;
import cn.lanyue.cas.entity.HousingEstateFamily;
import cn.lanyue.cas.utils.Validator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class UserBaseInfoVo extends BaseEntity implements Serializable {

    @NotBlank(groups = {AddGroup.class, GuardAddGroup.class, UpdateGroup.class})
    private String name;
    //@NotBlank(groups = {AddGroup.class, GuardAddGroup.class})
    private String mobilePhone;
    //@NotBlank(groups = {AddGroup.class, GuardAddGroup.class})
    private String idNumber;
    @NotBlank(groups = {AddGroup.class, GuardAddGroup.class})
    private String openId;

    //private String numberPlate;
    private List<Car> cars;

    @NotBlank(groups = {AddGroup.class, GuardAddGroup.class, UpdateGroup.class})
    private String housingEstateId;
    @NotBlank(groups = {AddGroup.class})
    private String building;
    //@NotBlank(groups = {AddGroup.class})
    private String unit;
    @NotBlank(groups = {AddGroup.class})
    private String roomNumber;

    /**短信验证码*/
    private String verifyCode;

    private String familyId;

    //private Boolean isRegist;

    public  final HousingEstateFamily buildFamily() {
        HousingEstateFamily housingEstateFamily = new HousingEstateFamily();
        housingEstateFamily.setBuilding(this.building);
        housingEstateFamily.setUnit(this.unit);
        housingEstateFamily.setRoomNumber(this.roomNumber);
        housingEstateFamily.setEstateId(this.housingEstateId);
        return housingEstateFamily;
    }

    public  final BaseUser buildUser() {
        BaseUser baseUser = new BaseUser();
        baseUser.setIdNumber(this.idNumber);
        baseUser.setName(this.name);
        baseUser.setMobilePhone(this.mobilePhone);
        baseUser.setOpenId(this.openId);
        baseUser.setCars(this.cars);
        baseUser.setId(this.id);
        return baseUser;
    }

    public  final List<Car> buildCars(String userId) {

        if (Validator.isNullOrEmpty(this.cars)) {
            return Collections.emptyList();
        }

        for (Car car : this.cars) {
            car.setUserId(userId);
            car.setEstateId(this.housingEstateId);
        }
        return this.cars;
    }


}
