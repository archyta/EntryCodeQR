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
* 车实体
*/
@Table(name = "car")
@Data
public class Car extends DataEntity<Car> {

private static final long serialVersionUID = 1L;

        /**
        * 用户id
        */
        private String userId;

        /**
        * 车牌号
        */
        private String plateNumber;

        /**
         * 小区id
         */
        private String estateId;



}