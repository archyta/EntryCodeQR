package cn.lanyue.cas.vo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author 
 * @Description TODO
 * @Date 2020/3/2 18:33
 */
@Getter
@Setter
public class BaseUserInfoParam {

    @NotBlank
    private String estateId;
    @NotBlank
    private String familyId;
    @NotBlank
    private String userId;

}
