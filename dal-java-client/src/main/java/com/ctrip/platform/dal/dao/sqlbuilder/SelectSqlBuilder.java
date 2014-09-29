package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class SelectSqlBuilder extends AbstractSqlBuilder {
	
	private StringBuilder sql = new StringBuilder("SELECT ");
	
	private String tableName = "";
	
	private List<String> selectField =  new ArrayList<String>();
	
	private String orderByField =  "";
	
	//是否升序
	private boolean ascending = true;
	
	private boolean isPagination = false;
	
	/**
	 * 
	 * @param tableName 表名
	 * @param dBCategory 数据库类型
	 * @param isPagination 是否分页
	 * @throws SQLException
	 */
	public SelectSqlBuilder(String tableName,
			DatabaseCategory dBCategory, boolean isPagination)
			throws SQLException {
		super(dBCategory);
		if (tableName != null && !tableName.isEmpty()) {
			this.tableName = tableName;
		} else {
			throw new SQLException("table name is illegal.");
		}
		this.isPagination = isPagination;
	}
	
	/**
	 * 添加select字段
	 * @param fieldName
	 * @return
	 */
	public SelectSqlBuilder addSelectField(String ...fieldName){
		for(String field:fieldName){
			selectField.add(field);
		}
		return this;
	}
	
	/**
	 * 追加order by字段
	 * @param fieldName 字段名
	 * @param ascending 是否升序
	 * @return
	 */
	public SelectSqlBuilder addOrderbyField(String fieldName, boolean ascending){
		this.orderByField = fieldName;
		this.ascending = ascending;
		return this;
	}
	
	/**
	 * 构建SQL语句
	 * @return
	 */
	public String build(){
		if(this.isPagination && DatabaseCategory.MySql == this.dBCategory){
			return this.buildPaginationSql4MySQL();
		}
		if(this.isPagination && DatabaseCategory.SqlServer == this.dBCategory){
			return this.buildPaginationSql4SqlServer();
		}
		return this.buildSelectSql();
	}
	
	private String buildSelectSql(){
		sql = new StringBuilder("SELECT ");
		sql.append(buildSelectField());
		sql.append(" FROM ").append(this.wrapTableName(tableName));
		sql.append(" ").append(this.getWhereExp());
		sql.append(" ").append(buildOrderbyExp());
		return sql.toString();
	}
	
	private String buildPaginationSql4MySQL(){
		return buildSelectSql()+" limit %s, %s";
	}
	
	private String buildPaginationSql4SqlServer(){
		sql = new StringBuilder("SELECT ");
		String rowColumnField = "ROW_NUMBER() OVER ( " + buildOrderbyExp() + ") AS rownum";
		selectField.add(rowColumnField);
		sql.append(buildSelectField());
		sql.append(" FROM ").append(this.wrapTableName(tableName));
		sql.append(" ").append(this.getWhereExp());
		String cetWrap = "WITH CET AS (" + sql.toString() + ")";
		
		selectField.remove(selectField.size()-1);
		sql = new StringBuilder(cetWrap+" SELECT ");
		sql.append(buildSelectField());
		String temp = this.tableName;
		this.tableName = "CET";
		sql.append(" FROM ").append(tableName);
		this.tableName = temp;
		sql.append(" WHERE rownum BETWEEN %s AND %s");
		
		return sql.toString();
	}
	
	private String buildSelectField(){
		StringBuilder selectFields = new StringBuilder();
		for(int i=0,count=selectField.size();i<count;i++){
			selectFields.append(this.wrapField(selectField.get(i)));
			if(i<count-1){
				selectFields.append(", ");
			}
		}
		return selectFields.toString();
	}
	
	private String buildOrderbyExp(){
		StringBuilder orderbyExp = new StringBuilder();
		if(orderByField!=null && orderByField.length()>0){
			orderbyExp.append("ORDER BY ");
			String wrap = this.wrapField(orderByField);
			wrap = ascending? wrap+" ASC":wrap+" DESC";
			orderbyExp.append(wrap);
		}
		return orderbyExp.toString();
	}
	
	
	public static void main(String[] args) throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		SelectSqlBuilder builder = new SelectSqlBuilder("People", DatabaseCategory.MySql, false);
		builder.addSelectField("PeopleID","Name","CityID");
		builder.equal("a", "paramValue", Types.INTEGER);
		
		builder.and().in("b", in, Types.INTEGER);
		
		builder.and().like("b", "in", Types.INTEGER);
		
		builder.and().betweenNullable("c", "paramValue1", "paramValue2", Types.INTEGER);
		
		builder.and().betweenNullable("d", null, "paramValue2", Types.INTEGER);
		
		builder.and().isNull("sss");
		
		builder.addOrderbyField("PeopleID", false);
		String sql = builder.build();
		System.out.println(sql);
		System.out.println(builder.getStatementParameterIndex());
		
		System.out.println(builder.buildPaginationSql4MySQL());
		System.out.println(builder.buildPaginationSql4SqlServer());
		System.out.println(builder.buildPaginationSql4MySQL());
		
	}

}










