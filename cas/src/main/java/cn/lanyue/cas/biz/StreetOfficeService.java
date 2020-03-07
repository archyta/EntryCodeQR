package cn.lanyue.cas.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.StreetOfficeMapper;
import cn.lanyue.cas.entity.StreetOffice;

/**
 * 街道办表Service
 */
@Service
@Transactional(readOnly = true)
public class StreetOfficeService extends BaseBiz<StreetOfficeMapper, StreetOffice> {
	
}
