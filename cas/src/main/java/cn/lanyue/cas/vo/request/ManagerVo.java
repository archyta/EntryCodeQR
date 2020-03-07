package cn.lanyue.cas.vo.request;

import cn.lanyue.cas.core.validation.AddGroup;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/23 17:44
 */
@Getter
@Setter
public class ManagerVo implements Serializable {

    /**
     * 被添加的用户id
     */
    @NotBlank(groups = {AddGroup.class})
    private String userId;

    /**
     * 被添加的小区id
     */
    @NotBlank(groups = {AddGroup.class})
    private String estateId;


}
