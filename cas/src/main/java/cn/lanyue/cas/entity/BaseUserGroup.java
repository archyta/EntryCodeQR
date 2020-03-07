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
* 用户-用户组表实体
*/
@Table(name = "base_user_group")
@Data
public class BaseUserGroup extends DataEntity<BaseUserGroup> {

private static final long serialVersionUID = 1L;

        /**
        * 用户id
        */
        private String userId;
        /**
        * 用户组id
        */
        private String groupId;
        /**
        * 保存小区id
        */
        private String attr1;
        /**
        * 预留字段2
        */
        private String attr2;
        /**
        * 预留字段3
        */
        private String attr3;
        /**
        * 预留字段4
        */
        private String attr4;



}