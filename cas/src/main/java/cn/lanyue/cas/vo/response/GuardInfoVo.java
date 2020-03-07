package cn.lanyue.cas.vo.response;


import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.HousingEstate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GuardInfoVo implements Serializable {

    private HousingEstate estate;

    private BaseUser guardUser;

    public GuardInfoVo(HousingEstate housingEstate, BaseUser baseUser) {
        this.estate = housingEstate;
        this.guardUser = baseUser;
    }
}
