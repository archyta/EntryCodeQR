package cn.lanyue.cas.model;

import java.util.List;

public class Model {

	private String modelName;
	private boolean tree;
	private String packageName;
	private List<Column> columnList = null;
	
	
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
	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}
}
