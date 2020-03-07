package cn.lanyue.cas.builder;

import cn.lanyue.cas.common.Constants;
import cn.lanyue.cas.model.*;
import cn.lanyue.cas.utils.GeneratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jacktom
 */
public class GeneratorBuilder {

	private Model model = null;
	private Dao dao = null;
	private String tableComment;
	private Mapper mapper;
	private Service service;

	private Logger logger = LoggerFactory.getLogger(GeneratorBuilder.class);

	public GeneratorBuilder(String pkgName, String clzName, Table table, String tableComment) {
		this.tableComment = tableComment;

		model = new Model();
		if (pkgName == null) {
			model.setPackageName(Constants.PKG_PREFIX + "entity");
		} else {
			model.setPackageName(Constants.PKG_PREFIX + pkgName + ".entity");
		}
		model.setModelName(clzName);

		dao = new Dao();
		if (pkgName == null) {
			dao.setPackageName(Constants.PKG_PREFIX + "mapper");
		} else {
			dao.setPackageName(Constants.PKG_PREFIX + pkgName + ".mapper");
		}
		dao.setModelName(clzName);

		mapper = new Mapper();
		if (pkgName == null) {
			mapper.setPackageName(Constants.PKG_PREFIX + "mapper");
		} else {
			mapper.setPackageName(Constants.PKG_PREFIX + pkgName + ".mapper");
		}
		mapper.setModelName(clzName);

		service = new Service();
		if (pkgName == null) {
			service.setPackageName(Constants.PKG_PREFIX + "biz");
		} else {
			service.setPackageName(Constants.PKG_PREFIX + pkgName + ".biz");
		}
		service.setModelName(clzName);

		List<Column> list = table.getColumnList();

		mapper.setColumnList(list);
		mapper.setTableName(table.getTableName());
		mapper.setModelPackageName(model.getPackageName());

		// 判断表格是否有树节点
		/*
		 * Column treeParent = new Column(); treeParent.setName("parent_id"); if
		 * (list.contains(treeParent)) { model.setTree(true); mapper.setTree(true); }
		 * else { model.setTree(false); mapper.setTree(false); }
		 */
		model.setTree(false);
		mapper.setTree(false);

		List<Column> columnList = new ArrayList<Column>();
		for (Column c : list) {

			if (StringUtils.equals("id", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("crtUser", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("crtTime", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("updUser", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("updTime", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("delFlag", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("crtName", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("updName", c.getColumnName())) {
				continue;
			} else {
				columnList.add(c);
			}

		}

		model.setColumnList(columnList);

		List<Column> insertColumnList = new ArrayList<Column>();
		for (Column c : list) {
			insertColumnList.add(c);
		}
		mapper.setInsertColumnList(insertColumnList);

		List<Column> updateColumnList = new ArrayList<Column>();
		for (Column c : list) {
			if (StringUtils.equals("id", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("crtUser", c.getColumnName())) {
				continue;
			} else if (StringUtils.equals("crtTime", c.getColumnName())) {
				continue;
			} else {
				updateColumnList.add(c);
			}
		}
		mapper.setUpdateColumnList(updateColumnList);
	}

	/**
	 * @param isCreateDaoAndService 是否创建Dao和Service
	 */
	public void build(boolean isCreateDaoAndService) {
		Map<String, Object> root = new HashMap<String, Object>();
		String modelFileName = GeneratorUtils.getTargetFilePath(model.getPackageName(), model.getModelName(),
				Constants.JAVA_SUFFIX);
		String daoFileName = GeneratorUtils.getTargetFilePath(dao.getPackageName(), dao.getModelName() + "Mapper",
				Constants.JAVA_SUFFIX);

		String serviceFileName = GeneratorUtils.getTargetFilePath(service.getPackageName(),
				service.getModelName() + "Service", Constants.JAVA_SUFFIX);
		String mapFileName = GeneratorUtils.getTargetFilePath(dao.getPackageName(), dao.getModelName() + "Mapper",
				Constants.MAPPINGS_SUFFIX);
		String modelFilePath = GeneratorUtils.getOutputPath(modelFileName);
		String daoFilePath = GeneratorUtils.getOutputPath(daoFileName);
		String serviceFilePath = GeneratorUtils.getOutputPath(serviceFileName);
		String mapFilePath = GeneratorUtils.getOutputPath(mapFileName);

		root.put("model", model);
		root.put("dao", dao);
		root.put("mapper", mapper);
		root.put("tableComment", tableComment);

		root.put("modelNameLowerCase",
				model.getModelName().substring(0, 1).toLowerCase() + model.getModelName().substring(1));
		try {
			GeneratorUtils.output("Model.ftl", root, modelFilePath);
			if (isCreateDaoAndService) {
				GeneratorUtils.output("Dao.ftl", root, daoFilePath);
				root.put("service", service);
				GeneratorUtils.output("Service.ftl", root, serviceFilePath);
			}
			if (model.getTree()) {
				GeneratorUtils.output("MapperTree.ftl", root, mapFilePath);
			} else {
				GeneratorUtils.output("MapperCrud.ftl", root, mapFilePath);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
}
