package cn.lanyue.cas.model;

public class Column {

	private String name;
	private String nameUp;
	private String type;
	private int size;
	private int precision;
	private int nullable;  // 0 : 可以为null，1：不能为null
	private String comments;
	private String columnName;
	private String columnType;

	private String columnNameU;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getNameUp() {
		return nameUp;
	}
	public void setNameUp(String nameUp) {
		this.nameUp = nameUp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public int getNullable() {
		return nullable;
	}
	public void setNullable(int nullable) {
		this.nullable = nullable;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getColumnNameU() {
		return columnNameU;
	}
	public void setColumnNameU(String columnNameU) {
		this.columnNameU = columnNameU;
	}
	
	
}
