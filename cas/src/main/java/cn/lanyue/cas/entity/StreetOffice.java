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
* 街道办表实体
*/
@Table(name = "street_office")
@Data
public class StreetOffice extends DataEntity<StreetOffice> {

private static final long serialVersionUID = 1L;

        /**
        * 名称
        */
        private String name;
        /**
        * 地址
        */
        private String address;
        /**
        * 责任人
        */
        private String responsible;
        /**
        * 责任人电话
        */
        private String responsibleMobilePhone;

        /**
         * 属于社区(街道)的二维码
         */
        private String miniAppQrcode;


}