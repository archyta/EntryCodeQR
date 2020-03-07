package cn.lanyue.cas.vo.request;

import cn.lanyue.cas.entity.HealthStatus;
import cn.lanyue.cas.entity.TripRecord;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;


@Getter
@Setter
public class ExamineVo implements Serializable {

    @Valid
    private TripRecord tripRecord;

    @Valid
    private HealthStatus healthStatus;
}
