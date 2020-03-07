package cn.lanyue.cas.entity;

import cn.lanyue.cas.common.Constant;
import cn.lanyue.cas.core.entity.DataEntity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import cn.lanyue.cas.core.validation.AddGroup;
import com.google.common.base.Preconditions;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;

/**
* 体温登记实体
*/
@Table(name = "temperature_record")
@Data
public class TemperatureRecord extends DataEntity<TemperatureRecord> {

private static final long serialVersionUID = 1L;

        /**
        * 用户id
        */
        @NotBlank(groups = {AddGroup.class})
        private String userId;
        /**
        * 体温
        */
        @NotNull(groups = {AddGroup.class})
        private BigDecimal temperature;
        /**
        * 家庭id
        */
        @NotBlank(groups = {AddGroup.class})
        private String familyId;
        /**
        * 小区id
        */
        @NotBlank(groups = {AddGroup.class})
        private String estateId;

        @Transient
        private String userName;

        /**
         * 上午还是下午
         */
        private String timeInterval;


        /**
         * 体温异常标识
         * 0：正常
         * 1：风险
         * 2：异常
         */
        @Transient
        private String status;

        public TemperatureRecord() {

        }

        public TemperatureRecord(Date date, String timeInterval) {
                this.crtTime = date;
                this.timeInterval = timeInterval;
        }

        public String checkStatus(BigDecimal risk, BigDecimal abnormal) {
                Preconditions.checkArgument(risk != null, "risk cannot be null");
                Preconditions.checkArgument(abnormal != null, "abnormal cannot be null");
                //正常
                int isRisk = this.temperature.compareTo(risk);
                if (isRisk < 0) {
                        return Constant.normal;
                }

                if (isRisk >= 0 && this.temperature.compareTo(abnormal) < 0) {
                        return Constant.risk;
                }
                return Constant.abnormal;
        }

}