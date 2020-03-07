package cn.lanyue.cas.entity;

import cn.lanyue.cas.biz.valid.SelfEntryGroup;
import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import cn.lanyue.cas.core.validation.AddGroup;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import javax.persistence.Table;

/**
* 出行记录表实体
*/
@Table(name = "trip_record")
@Data
public class TripRecord extends DataEntity<TripRecord> {

private static final long serialVersionUID = 1L;

        /**
        * 门卫审核状态 由数据字典定义
        */
        @NotBlank(groups = {AddGroup.class, SelfEntryGroup.class})
        private String status;
        /**
        * 用户id
        */
        @NotBlank(groups = {AddGroup.class, SelfEntryGroup.class})
        private String userId;
        /**
        * 出行方向 0->进；1->出
        */
        @NotBlank(groups = {AddGroup.class, SelfEntryGroup.class})
        private String direction;

        /**
        * 家庭id
        */
        @NotBlank(groups = {AddGroup.class, SelfEntryGroup.class})
        private String familyId;

        /**
         * 小区id
         */
        @NotBlank(groups = {AddGroup.class, SelfEntryGroup.class})
        private String estateId;

        /**
         * 门卫id
         */
        @NotBlank(groups = {AddGroup.class})
        private String guardId;

        //区块链参数
        private String hash;

        private String height;



        @Transient
        private String estateName;

        @Transient
        private String userName;

        @Transient
        private String mobilePhone;

}