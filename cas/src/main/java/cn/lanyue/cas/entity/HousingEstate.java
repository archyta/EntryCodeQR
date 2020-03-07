package cn.lanyue.cas.entity;

import cn.lanyue.cas.core.entity.DataEntity;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
* 小区表实体
*/
@Table(name = "housing_estate")
@Data
public class HousingEstate extends DataEntity<HousingEstate> {

private static final long serialVersionUID = 1L;

        /**
        * 全称
        */
        private String name;

        /**
        * 地址
        */
        private String address;

        /**
        * 街道办id
        */
        private String streetOfficeId;

        /**
         * 门头照路径
         */
        private String headPhotoPath;

        /**
         * 小区二维码，一个小区一个
         */
        private String miniAppQrcode;

        private String miniAppGuardQrcode;

        /**
         * 门卫海报生成
         */
        private String guardPoster;

        /**
         * 住户海报生成
         */
        private String ownerPoster;

        /**
         * 代理商id
         */
        private String agentId;

        /**
         * 门岗控制模式
         */
        private String guardControlPattern;

        //@Transient
        private String streetOfficeName;

        /**
         * 风险体温阈值
         */
        private BigDecimal riskTemperature;

        /**
         * 异常体温阈值
         */
        private BigDecimal abnormalTemperature;

        @Transient
        private List<HousingEstateFamily> families;

        @Transient
        private List<DataRule> dataRules;

        @Transient
        private List<String> groupCodes;

        @Transient
        private BaseUser crtUser;

        //小区保安人数
        @Transient
        private int guardNum;

}