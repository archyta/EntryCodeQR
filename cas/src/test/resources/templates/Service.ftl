package ${service.packageName};

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import ${dao.packageName}.${dao.modelName}Mapper;
import ${model.packageName}.${model.modelName};

/**
 * ${tableComment}Service
 */
@Service
@Transactional(readOnly = true)
public class ${dao.modelName}Service extends BaseBiz<${dao.modelName}Mapper, ${model.modelName}> {
	
}
