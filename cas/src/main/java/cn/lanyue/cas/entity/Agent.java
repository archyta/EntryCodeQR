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
* 代理商实体
*/
@Table(name = "agent")
@Data
public class Agent extends DataEntity<Agent> {

private static final long serialVersionUID = 1L;

        /**
        * 代理商名称
        */
        private String name;
        /**
        * 电话
        */
        private String mobilePhone;
        /**
        * 创建人
        */
        private String crtBy;
        /**
        * 更新人
        */
        private String updBy;


}