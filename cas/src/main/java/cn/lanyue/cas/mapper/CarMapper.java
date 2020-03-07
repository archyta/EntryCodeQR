package cn.lanyue.cas.mapper;

import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;
import cn.lanyue.cas.entity.Car;

/**
 * 车DAO接口
 */
public interface CarMapper extends  Mapper<Car>, InsertListMapper<Car> {
	
}
