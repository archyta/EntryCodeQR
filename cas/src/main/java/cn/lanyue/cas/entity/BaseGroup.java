package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;

/**
* 用户组实体
*/
@Table(name = "base_group")
@Data
public class BaseGroup extends DataEntity<BaseGroup> {

private static final long serialVersionUID = 1L;

        /**
        * 角色编码
        */
        private String code;
        /**
        * 角色名称
        */
        private String name;
        /**
        * 父id
        */
        private String parentId;
        /**
        * 父ids
        */
        private String parentIds;
        /**
        * 树形关系
        */
        private String path;

        /**
        * 类型
        */
        private String type;
        /**
        * 用户组类型
        */
        private String groupType;
        /**
        * 描述
        */
        private String description;

        /**
         * 锁定
         */
        private String isLock;

        /**
        * 数据范围
        */
        private String dataScope;


        @Transient
        private String estateId;



}