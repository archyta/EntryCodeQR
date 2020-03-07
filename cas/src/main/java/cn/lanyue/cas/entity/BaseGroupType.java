package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;

/**
* 用户组类型实体
*/
@Table(name = "base_group_type")
@Data
public class BaseGroupType extends DataEntity<BaseGroupType> {

private static final long serialVersionUID = 1L;

        /**
        * 编码
        */
        private String code;
        /**
        * 名称
        */
        private String name;
        /**
        * 描述
        */
        private String description;
        /**
        * 创建人
        */
        private String crtBy;
        /**
        * 更新人
        */
        private String updBy;


}