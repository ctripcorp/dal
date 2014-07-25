package com.ctrip.platform.dal.daogen.utils;

import java.io.StringReader;
import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;

public class SqlBuilder {
	
	private static final String mysqlPageClausePattern = " limit %s, %s";
	private static final String mysqlCSPageClausePattern = " limit {0}, {1}";
	private static final String sqlserverPageClausePattern = " rownum BETWEEN %s AND %s";
	private static final String sqlserverCSPageClausePattern = " rownum BETWEEN {0} AND {1}";
	
	private static CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	/**
	 * Re-build the query SQL to implement paging function.
	 * The new SQL Statement will contains limit if the database type is MYSQL, 
	 * CET wrapped if database type is SQL Server. 
	 * Note: the final SQL will contain two %s, which should be replaced in run time.
	 * @param sql
	 * 		The original SQL Statement
	 * @param dbType
	 * 		The database type
	 * @return
	 * 		Re-build SQL which contains limit if the database type is MYSQL, CET wrapped if database type is SQL Server.
	 * @throws Exception
	 */
	public static String pagingQuerySql(String sql, DatabaseCategory dbType, CurrentLanguage lang) throws Exception{
		Select select = (Select) parserManager.parse(new StringReader(sql.replace("@", ":")));
		PlainSelect plain = (PlainSelect)select.getSelectBody();
		String result = "";
		if(dbType == DatabaseCategory.MySql){	
			result = plain.toString() + 
					(lang == CurrentLanguage.Java ? mysqlPageClausePattern : mysqlCSPageClausePattern);
		}else if(dbType == DatabaseCategory.SqlServer){		
			List<OrderByElement> orderbys = plain.getOrderByElements();
			List<SelectItem> selectitems = plain.getSelectItems();
			if(null == orderbys || orderbys.size() != 1){
				throw new Exception("The sql server CET paging must contain one order clause.");
			}
			String rowColumn = "ROW_NUMBER() OVER (ORDER BY " + orderbys.get(0).toString() + ") AS rownum";
			SelectExpressionItem newItem = new SelectExpressionItem(new Column(rowColumn));
			selectitems.add(newItem);
			plain.getOrderByElements().clear();
			
			String cetWrap = "WITH CET AS (" + plain.toString() + ")";
			
			selectitems.remove(newItem);
			result = cetWrap + " SELECT " + StringUtils.join(selectitems, ", ") + " FROM CET WHERE " + 
			(lang == CurrentLanguage.Java ? sqlserverPageClausePattern : sqlserverCSPageClausePattern);
		}else{
			throw new Exception("Unknow database category.");
		}
		
		return result.replace(":", "@");
	}
	
	
	public static void main(String[] args) throws Exception{
		String sql = "SELECT Birth,Name,Age,Telephone,PartmentID,Gender,Address,ID,space FROM Person WHERE  Age > @Age order by Name";
		String cet = pagingQuerySql(sql, DatabaseCategory.SqlServer, CurrentLanguage.Java);
		
		System.out.println(cet);
	}
	
//	public static String formatSql(GenTaskBySqlBuilder task) {
//
//		// 数据库中存储的模式： ID,0;Name,1; 表示"ID = "以及"Name != "
//		String[] conditions = task.getCondition().split(";");
//		// 数据库中存储的模式： ID,Name
//		String[] fields = task.getFields().split(",");
//
//		List<String> formatedConditions = new ArrayList<String>();
//		// 将所有WHERE条件拼接，如ID_0,Name_1，for循环后将变为一个数组： [" ID = ", " Name != "]
//		for (String con : conditions) {
//			String[] keyValue = con.split(",");
//			if (keyValue.length != 2) {
//				continue;
//			}
//			// Between类型的操作符需要特殊处理
//			if (keyValue[1].equals("6")) {
//				if (task.getSql_style().equals("csharp")) {
//					formatedConditions.add(String.format(
//							" BETWEEN @%s_start AND @%s_end ", keyValue[0],
//							keyValue[0]));
//				} else {
//					formatedConditions.add(" BETWEEN ? AND ? ");
//				}
//			} else {
//				if (task.getSql_style().equals("csharp")) {
//					formatedConditions.add(String.format(" %s %s @%s ",
//							keyValue[0],
//							Consts.WhereConditionMap.get(keyValue[1]),
//							keyValue[0]));
//				} else {
//					formatedConditions.add(String.format(" %s %s ? ",
//							keyValue[0],
//							Consts.WhereConditionMap.get(keyValue[1])));
//				}
//			}
//		}
//
//		if (task.getCrud_type().equalsIgnoreCase("Select")) {
//			if (formatedConditions.size() > 0) {
//				return String
//						.format("SELECT %s FROM %s WHERE %s", task.getFields(),
//								task.getTable_name(), StringUtils.join(
//										formatedConditions.toArray(), " AND "));
//			} else {
//				return String.format("SELECT %s FROM %s", task.getFields(),
//						task.getTable_name());
//			}
//		} else if (task.getCrud_type().equalsIgnoreCase("Insert")) {
//
//				List<String> placeHodler = new ArrayList<String>();
//				for (String field : fields) {
//					if (task.getSql_style().equals("csharp")) {
//						placeHodler.add(String.format(" @%s ", field));
//					} else {
//						placeHodler.add(" ? ");
//					}
//				}
//				return String.format("INSERT INTO %s (%s) VALUES (%s)",
//						task.getTable_name(), task.getFields(),
//						StringUtils.join(placeHodler.toArray(), ","));
//			
//
//		} else if (task.getCrud_type().equalsIgnoreCase("Update")) {
//				List<String> placeHodler = new ArrayList<String>();
//				for (String field : fields) {
//					if (task.getSql_style().equals("csharp")) {
//						placeHodler.add(String.format(" %s = @%s ", field,
//								field));
//					} else {
//						placeHodler.add(String.format(" %s = ? ", field));
//					}
//				}
//				if (formatedConditions.size() > 0) {
//					return String.format("UPDATE %s SET %s WHERE %s", task
//							.getTable_name(), StringUtils.join(
//							placeHodler.toArray(), ","), StringUtils.join(
//							formatedConditions.toArray(), " AND "));
//				} else {
//					return String.format("UPDATE %s SET %s ",
//							task.getTable_name(),
//							StringUtils.join(placeHodler.toArray(), ","));
//				}
//
//		} else if (task.getCrud_type().equalsIgnoreCase("Delete")) {
//				if (formatedConditions.size() > 0) {
//					return String.format("Delete FROM %s WHERE %s", task
//							.getTable_name(), StringUtils.join(
//							formatedConditions.toArray(), " AND "));
//				} else {
//					return String
//							.format("Delete FROM %s", task.getTable_name());
//				}
//		}
//
//		return "";
//
//	}
}
