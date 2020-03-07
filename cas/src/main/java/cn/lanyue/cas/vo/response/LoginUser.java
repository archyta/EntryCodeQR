package cn.lanyue.cas.vo.response;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.lanyue.cas.entity.BaseGroup;
import cn.lanyue.cas.entity.BaseUser;
import cn.lanyue.cas.entity.HousingEstate;
import cn.lanyue.cas.utils.Validator;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LoginUser implements Serializable {

    private String sessionKey;

    private String openid;

    private String unionid;

    private List<HousingEstate> estates;


    private String userId;

    /**
     * 角色组编码
     */
    private List<String> groupCodes;



    public static LoginUser build(BaseUser baseUser, List<HousingEstate> estates, WxMaJscode2SessionResult wxMaJscode2SessionResult) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(baseUser.getId());
        loginUser.setOpenid(wxMaJscode2SessionResult.getOpenid());
        loginUser.setSessionKey(wxMaJscode2SessionResult.getSessionKey());
        loginUser.setUnionid(wxMaJscode2SessionResult.getUnionid());
        loginUser.setEstates(estates);

        return loginUser;
    }

}
