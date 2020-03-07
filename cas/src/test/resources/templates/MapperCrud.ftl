<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${dao.packageName}.${dao.modelName}Mapper">

	<sql id="${modelNameLowerCase}ColumnsNotA">
	<#if (mapper.columnList)??>
	<#list mapper.columnList as column>
		<#if column.name=='parent_id'>parent_id AS "parentId"<#else>${column.name} as ${column.columnName}</#if><#if column_has_next>,</#if>
	</#list>
	</#if>
	</sql>

</mapper>