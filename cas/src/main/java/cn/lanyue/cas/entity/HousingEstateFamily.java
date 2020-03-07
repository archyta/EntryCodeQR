package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Table;

/**
* 家庭表实体
*/
@Table(name = "housing_estate_family")
@Data
public class HousingEstateFamily extends DataEntity<HousingEstateFamily> {

private static final long serialVersionUID = 1L;


        /**
        * 小区id
        */
        private String estateId;
        /**
        * 类型
        */
        private String type;

        /**
         *
         */
        private String mainUserId;
        /**
        * 楼栋
        */
        private String building;
        /**
        * 单元
        */
        private String unit;
        /**
        * 房号
        */
        private String roomNumber;


        @Transient
        private List<BaseUser> familyMembers;

        /**
         * 小区名称
         */
        @Transient
        private String estateName;

}