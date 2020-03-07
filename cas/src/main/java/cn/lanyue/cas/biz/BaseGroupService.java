package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.BaseGroupMapper;
import cn.lanyue.cas.entity.BaseGroup;

/**
 * 用户组Service
 */
@Service
@Transactional(readOnly = true)
public class BaseGroupService extends BaseBiz<BaseGroupMapper, BaseGroup> {
	
}
