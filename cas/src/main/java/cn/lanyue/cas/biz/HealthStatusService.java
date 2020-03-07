package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.HealthStatusMapper;
import cn.lanyue.cas.entity.HealthStatus;

/**
 * 健康状态表Service
 */
@Service
@Transactional(readOnly = true)
public class HealthStatusService extends BaseBiz<HealthStatusMapper, HealthStatus> {
	
}
