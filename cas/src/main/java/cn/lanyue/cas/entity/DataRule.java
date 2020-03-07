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
* 数据规则实体
*/
@Table(name = "data_rule")
@Data
public class DataRule extends DataEntity<DataRule> {

private static final long serialVersionUID = 1L;

        /**
        * 规则元类型
        */
        private String type;
        /**
        * 规则元名称
        */
        private String name;
        /**
        * 表名称(也可为别名)
        */
        private String tableName;

        /**
         * 值
         */
        private String value;

        /**
        * 表字段
        */
        private String field;
        /**
        * 创建人
        */
        private String crtBy;
        /**
        * 更新人
        */
        private String updBy;

        public static List<DataRule> defaultEstateConfig() {
                List<DataRule> defaultValue = Lists.newArrayListWithCapacity(3);

                DataRule familyDataRule = new DataRule();
                familyDataRule.setType("familyType");
                familyDataRule.setValue("family");

                DataRule daysDataRule = new DataRule();
                daysDataRule.setType("days");
                daysDataRule.setValue("30");

                DataRule countDataRule = new DataRule();
                countDataRule.setType("accessCount");
                countDataRule.setValue("30");

                defaultValue.add(familyDataRule);
                defaultValue.add(daysDataRule);
                defaultValue.add(countDataRule);

                return defaultValue;
        }

}