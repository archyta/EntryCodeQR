package cn.lanyue.cas.model;

import java.util.List;

public class Mapper {

	private String modelName;
	private boolean tree;
	private String packageName;
	private List<Column> columnList = null;
	private List<Column> insertColumnList = null;
	private List<Column> updateColumnList = null;
	private String tableName;
	private String modelPackageName;
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public boolean getTree() {
		return tree;
	}
	public void setTree(boolean isTree) {
		this.tree = isTree;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public List<Column> getColumnList() {
		return columnList;
	}
	
	public List<Column> getInsertColumnList() {
		return insertColumnList;
	}
	public void setInsertColumnList(List<Column> insertColumnList) {
		this.insertColumnList = insertColumnList;
	}
	public List<Column> getUpdateColumnList() {
		return updateColumnList;
	}
	public void setUpdateColumnList(List<Column> updateColumnList) {
		this.updateColumnList = updateColumnList;
	}
	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getModelPackageName() {
		return modelPackageName;
	}
	public void setModelPackageName(String modelPackageName) {
		this.modelPackageName = modelPackageName;
	}
	
}
