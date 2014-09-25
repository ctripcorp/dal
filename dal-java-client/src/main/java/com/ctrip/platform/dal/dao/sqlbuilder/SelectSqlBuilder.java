package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.StatementParameters;

public class SelectSqlBuilder {
	
	private StringBuilder sql = new StringBuilder("SELECT ");
	
	private String tableName = "";
	
	private List<String> selectField =  new ArrayList<String>();
	
	private String orderByExp =  "";
	
	private SqlWhereBuilder whereBuilder = new SqlWhereBuilder();
	
	public SelectSqlBuilder(String tableName) throws SQLException{
		if(tableName!=null && !tableName.isEmpty()){
			this.tableName = tableName;
		}else{
			throw new SQLException("table name is illegal.");
		}
	}
	
	public SelectSqlBuilder addSelectField(String ...fieldName){
		for(String field:fieldName){
			selectField.add(field);
		}
		return this;
	}
	
	public SqlWhereBuilder addConstrant(){
		return whereBuilder;
	}
	
	public SelectSqlBuilder addOrderByExp(String orderByExp){
		this.orderByExp = orderByExp;
		return this;
	}
	
	public String buildSelectSql(){
		buildSelectField();
		sql.append(" FROM ").append(tableName);
		sql.append(" ").append(whereBuilder.getWhereExp());
		sql.append(" ").append(orderByExp);
		return sql.toString();
	}
	
	public String buildPaginationSql4MySQL(){
		sql = new StringBuilder("SELECT ");
		return buildSelectSql()+" limit %s, %s";
	}
	
	public String buildPaginationSql4SqlServer(){
		sql = new StringBuilder("SELECT ");
		String rowColumnField = "ROW_NUMBER() OVER ( " + orderByExp + ") AS rownum";
		selectField.add(rowColumnField);
		buildSelectField();
		sql.append(" FROM ").append(tableName);
		sql.append(" ").append(whereBuilder.getWhereExp());
		String cetWrap = "WITH CET AS (" + sql.toString() + ")";
		
		selectField.remove(selectField.size()-1);
		sql = new StringBuilder(cetWrap+" SELECT ");
		buildSelectField();
		String temp = this.tableName;
		this.tableName = "CET";
		sql.append(" FROM ").append(tableName);
		this.tableName = temp;
		sql.append(" WHERE rownum BETWEEN %s AND %s");
		
		return sql.toString();
	}
	
	private void buildSelectField(){
		for(int i=0,count=selectField.size();i<count;i++){
			sql.append(selectField.get(i));
			if(i<count-1){
				sql.append(", ");
			}
		}
	}
	
	
	public static void main(String[] args) throws SQLException {
		List<String> in = new ArrayList<String>();
		in.add("12");
		in.add("12");
		SelectSqlBuilder builder = new SelectSqlBuilder("[HotelPubDB].[dbo].[People] with (nolock)");
		builder.addSelectField("[PeopleID]","[Name]","[CityID]");
		StatementParameters parameters = new StatementParameters();
		int index = 1;
		index = builder.addConstrant().equal("a", "paramValue", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().in("b", in, parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().like("b", "in", parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("c", "paramValue1", "paramValue2", 
				parameters, index, Types.INTEGER);
		
		index = builder.addConstrant().and().betweenNullable("d", null, "paramValue2", 
				parameters, index, Types.INTEGER);
		
		builder.addConstrant().and().isNull("sss");
		
		builder.addOrderByExp("ORDER BY [PeopleID] DESC");
		String sql = builder.buildSelectSql();
		System.out.println(sql);
		
		System.out.println(builder.buildPaginationSql4MySQL());
		System.out.println(builder.buildPaginationSql4SqlServer());
		System.out.println(builder.buildPaginationSql4MySQL());
		
	}

}










