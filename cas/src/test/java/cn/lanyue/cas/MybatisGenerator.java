package cn.lanyue.cas;



import cn.lanyue.cas.builder.GeneratorBuilder;
import cn.lanyue.cas.utils.DbMetaDataUtils;

import java.sql.SQLException;

public class MybatisGenerator {

    public static void main(String[] args) throws SQLException {
//        new GeneratorBuilder(null, "BaseGroup", DbMetaDataUtils.getTable("base_group"), "用户组").build(true);
//        new GeneratorBuilder(null, "BaseGroupType", DbMetaDataUtils.getTable("base_group_type"), "用户组类型").build(true);
//        new GeneratorBuilder(null, "BaseUser", DbMetaDataUtils.getTable("base_user"), "用户表").build(true);
//        new GeneratorBuilder(null, "BaseUserGroup", DbMetaDataUtils.getTable("base_user_group"), "用户-用户组表").build(true);
//        new GeneratorBuilder(null, "HealthStatus", DbMetaDataUtils.getTable("health_status"), "健康状态表").build(true);
//        new GeneratorBuilder(null, "HousingEstate", DbMetaDataUtils.getTable("housing_estate"), "小区表").build(true);
//        new GeneratorBuilder(null, "HousingEstateFamily", DbMetaDataUtils.getTable("housing_estate_family"), "家庭表").build(true);
//        new GeneratorBuilder(null, "HousingEstateGuard", DbMetaDataUtils.getTable("housing_estate_guard"), "小区保安表").build(true);
//        new GeneratorBuilder(null, "StreetOffice", DbMetaDataUtils.getTable("street_office"), "街道办表").build(true);
//        new GeneratorBuilder(null, "TripRecord", DbMetaDataUtils.getTable("trip_record"), "出行记录表").build(true);
//          new GeneratorBuilder(null, "EstateDataRule", DbMetaDataUtils.getTable("estate_data_rule"), "小区数据规则").build(true);
          new GeneratorBuilder(null, "Car", DbMetaDataUtils.getTable("car"), "车").build(true);



    }
}
