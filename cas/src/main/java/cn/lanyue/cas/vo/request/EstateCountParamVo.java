package cn.lanyue.cas.vo.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 19:37
 */
@Getter
@Setter
public class EstateCountParamVo {

    @NotBlank
    private String estateId;
    @NotBlank
    private String dataScope;
}
