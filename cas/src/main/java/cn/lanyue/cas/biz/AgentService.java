package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.AgentMapper;
import cn.lanyue.cas.entity.Agent;

/**
 * 代理商Service
 */
@Service
@Transactional(readOnly = true)
public class AgentService extends BaseBiz<AgentMapper, Agent> {
	
}
