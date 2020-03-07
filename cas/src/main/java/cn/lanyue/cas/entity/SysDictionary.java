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
* 数据字典实体
*/
@Table(name = "sys_dictionary")
@Data
public class SysDictionary extends DataEntity<SysDictionary> {

private static final long serialVersionUID = 1L;


        /**
         * 父编码
         */
        private String parentCode;

        /**
         * 名称
         */
        private String name;

        /**
         * 类型
         */
        private String type;

        /**
        * 字典编码
        */
        private String code;

        /**
        * 数据值
        */
        private String value;

        /**
        * 描述
        */
        private String description;

        /**
        * 排序
        */
        private String orderNum;

        /**
        * 是否锁定
        */
        private String isLock;

}