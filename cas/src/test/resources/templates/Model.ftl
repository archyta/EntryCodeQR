package ${model.packageName};

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;

/**
* ${tableComment}实体
*/
@Table(name = "${mapper.tableName}")
@Data
public class ${model.modelName} extends <#if model.tree>TreeEntity<#else>DataEntity</#if><${model.modelName}> {

private static final long serialVersionUID = 1L;

<#if (model.columnList)??>
    <#list model.columnList as column>
        /**
        * ${column.comments}
        */
        private ${column.columnType} ${column.columnName};
    </#list>
</#if>


}