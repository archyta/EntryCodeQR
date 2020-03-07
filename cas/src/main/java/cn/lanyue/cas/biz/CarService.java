package cn.lanyue.cas.biz;

import cn.lanyue.cas.core.utils.IdGen;
import cn.lanyue.cas.utils.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.CarMapper;
import cn.lanyue.cas.entity.Car;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.Date;
import java.util.List;

/**
 * 车Service
 */
@Service
@Transactional(readOnly = true)
public class CarService extends BaseBiz<CarMapper, Car> {


    public void addCars(List<Car> cars) {
        if (Validator.isNullOrEmpty(cars)) {
            return;
        }
        String userId = cars.get(0).getUserId();
        String estateId = cars.get(0).getEstateId();
        Example example = new Example.Builder(Car.class)
                .where(WeekendSqls.<Car>custom().andEqualTo(Car::getUserId, userId).andEqualTo(Car::getEstateId, estateId)).build();
        mapper.deleteByExample(example);

        for (Car car : cars) {
            car.setId(IdGen.uuid());
            car.setCrtTime(new Date());
            car.setUpdTime(new Date());
        }
        mapper.insertList(cars);

    }
}
