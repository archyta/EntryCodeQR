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
* 房屋-业主实体
*/
@Table(name = "housing_estate_family_user")
@Data
public class HousingEstateFamilyUser extends DataEntity<HousingEstateFamilyUser> {

private static final long serialVersionUID = 1L;

        /**
        * 
        */
        private String familyId;
        /**
        * 
        */
        private String userId;
        /**
        * 创建人
        */
        private String crtBy;
        /**
        * 更新人
        */
        private String updBy;


}