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
* 用户表实体
*/
@Table(name = "base_user")
@Data
public class BaseUser extends DataEntity<BaseUser> {

private static final long serialVersionUID = 1L;

        /**
        * 姓名
        */
        private String name;
        /**
        * 业主微信openid
        */
        private String openId;

        /**
        * 电话
        */
        private String mobilePhone;
        /**
        * 身份证号
        */
        private String idNumber;

        /**
         * 用户状态
         */
        private String status;

        /**
         * 车牌号
         */
        //private String numberPlate;

        /**
         * 所在所有小区
         */
        //@Transient
        //private List<HousingEstate> estates;

        @Transient
        private String familyId;

        @Transient
        private boolean isManager;

        @Transient
        private List<Car> cars;

}