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
* 小区保安表实体
*/
@Table(name = "housing_estate_guard")
@Data
public class HousingEstateGuard extends DataEntity<HousingEstateGuard> {

private static final long serialVersionUID = 1L;

        /**
        * 小区id
        */
        private String estateId;
        /**
        * 用户id
        */
        private String userId;


}