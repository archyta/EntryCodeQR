package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.BaseGroupTypeMapper;
import cn.lanyue.cas.entity.BaseGroupType;

/**
 * 用户组类型Service
 */
@Service
@Transactional(readOnly = true)
public class BaseGroupTypeService extends BaseBiz<BaseGroupTypeMapper, BaseGroupType> {
	
}
