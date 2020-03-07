package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Table;

/**
* 小区数据规则实体
*/
@Table(name = "estate_data_rule")
@Data
public class EstateDataRule extends DataEntity<EstateDataRule> {

private static final long serialVersionUID = 1L;

        /**
        * 
        */
        private String estateId;
        /**
        * 
        */
        private String ruleId;
        /**
        * 
        */
        private String ruleJson;
        /**
        * 创建人
        */
        private String crtBy;
        /**
        * 更新人
        */
        private String updBy;


}