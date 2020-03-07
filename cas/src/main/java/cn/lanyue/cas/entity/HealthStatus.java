package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import cn.lanyue.cas.core.validation.AddGroup;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
* 健康状态表实体
*/
@Table(name = "health_status")
@Data
public class HealthStatus extends DataEntity<HealthStatus> {

private static final long serialVersionUID = 1L;


        /**
        * 发热
        */
        @NotBlank(groups = {AddGroup.class})
        private String fever;
        /**
        * 乏力
        */
        @NotBlank(groups = {AddGroup.class})
        private String fatigue;
        /**
        * 干咳
        */
        @NotBlank(groups = {AddGroup.class})
        private String dryCough;
        /**
        * 体温
        */
        //@NotNull(groups = {AddGroup.class})
        private BigDecimal temperature;

        /**
         * 出行记录id
         */
        private String tripRecordId;




}