package com.ctrip.platform.dal.daogen.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;

/**
 * The SQL Re-build Utils
 * @author wcyuan
 */
public class SqlBuilder {
	
	private static Logger log = Logger.getLogger(SqlBuilder.class);
	
	private static final String regInEx = "(?i)In *\\(?\\?\\)?";
	private static final String regEx = "\\?";
	private static Pattern inRegxPattern = null;
	private static Pattern regxPattern = null;
	
	static{
		 inRegxPattern = Pattern.compile(regInEx);
		 regxPattern = Pattern.compile(regEx);
	}
	
	private static final String mysqlPageClausePattern = " limit %s, %s";
	private static final String mysqlCSPageClausePattern = " limit {0}, {1}";
	private static final String sqlserverPageClausePattern = " rownum BETWEEN %s AND %s";
	private static final String sqlserverCSPageClausePattern = " rownum BETWEEN {0} AND {1}";
	
	private static CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	public static String net2Java(String sql){
		return sql.replaceAll("@\\w+", "?");
	}
	
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
		String sql_content = sql.replace("@", ":");
		boolean withNolock = StringUtils.containsIgnoreCase(sql_content, "WITH (NOLOCK)");
		if(withNolock)
			sql_content = sql_content.replaceAll("(?i)WITH \\(NOLOCK\\)","");
		String result = "";
		try{
			Select select = (Select) parserManager.parse(new StringReader(sql_content));
			PlainSelect plain = (PlainSelect)select.getSelectBody();
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
				
				String sqlWithRowNum = plain.toString();
				if(withNolock){
					sqlWithRowNum = plainSelectToStringAppendWithNoLock(plain);
				}
				
				String cetWrap = "WITH CET AS (" + sqlWithRowNum + ")";
				
				selectitems.remove(newItem);
				List<String> selectField = new ArrayList<String>();
				for(int i=0;i<selectitems.size();i++){
					if("*".equalsIgnoreCase(selectitems.get(i).toString())){
						selectField.add("*");
						continue;
					}
					SelectExpressionItem item = (SelectExpressionItem) selectitems.get(i);
					if(item.getAlias() != null){
						selectField.add(item.getAlias().getName());
					}else{
						Column column = (Column) item.getExpression();
						selectField.add(column.getColumnName());
					}
				}
				result = cetWrap + " SELECT " + StringUtils.join(selectField, ", ") + " FROM CET WHERE " + 
				(lang == CurrentLanguage.Java ? sqlserverPageClausePattern : sqlserverCSPageClausePattern);
			}else{
				throw new Exception("Unknow database category.");
			}
		}catch(Exception e){
			log.error("Paging the SQL Failed.", e);
			throw e;
		}
		return result.replace(":", "@");
	}
	
	private static String plainSelectToStringAppendWithNoLock(PlainSelect plain){
		StringBuilder sql = new StringBuilder("SELECT ");
		if (plain.getDistinct() != null) {
			sql.append(plain.getDistinct()).append(" ");
		}
		if (plain.getTop() != null) {
			sql.append(plain.getTop()).append(" ");
		}
		sql.append(PlainSelect.getStringList(plain.getSelectItems()));
		if (plain.getFromItem() != null) {
			sql.append(" FROM ").append(plain.getFromItem());
			if (plain.getJoins() != null) {
				Iterator<Join> it = plain.getJoins().iterator();
				while (it.hasNext()) {
					Join join = it.next();
					if (join.isSimple()) {
						sql.append(", ").append(join);
					} else {
						sql.append(" ").append(join);
					}
				}
			}
			// sql += getFormatedList(joins, "", false, false);
			if (plain.getWhere() != null) {
				sql.append(" WITH (NOLOCK) WHERE ").append(plain.getWhere());
			}
			else{
				sql.append(" WITH (NOLOCK)");
			}
			if (plain.getOracleHierarchical() != null) {
				sql.append(plain.getOracleHierarchical().toString());
			}
			sql.append(PlainSelect.getFormatedList(plain.getGroupByColumnReferences(), "GROUP BY"));
			if (plain.getHaving() != null) {
				sql.append(" HAVING ").append(plain.getHaving());
			}
			sql.append(PlainSelect.orderByToString(plain.isOracleSiblings(), plain.getOrderByElements()));
			if (plain.getLimit() != null) {
				sql.append(plain.getLimit());
			}
		}
		return sql.toString();
	}
	
	public static void rebuildJavaInClauseSQL(String sql, List<JavaParameterHost> parameters) throws Exception{
		List<Integer> inStartIndex = new ArrayList<Integer>();
		List<Integer> inEndIndex = new ArrayList<Integer>();
		Matcher m = inRegxPattern.matcher(sql);
		while(m.find()){
			inStartIndex.add(m.start());
			inEndIndex.add(m.end());
		}
		if(inStartIndex.size() == 0)
			return;
		
		List<Integer> paramStartIndex = new ArrayList<Integer>();
		List<Integer> paramEndIndex = new ArrayList<Integer>();
		m = regxPattern.matcher(sql);
		while(m.find()){
			paramStartIndex.add(m.start());
			paramEndIndex.add(m.end());
		}
		
		if(paramStartIndex.size() != parameters.size())
			throw new Exception("The count of parameters is not correct");
		
		for (int i = 0; i < parameters.size(); i++) {
			for (int j = 0; j < inStartIndex.size(); j++) {
				if(paramStartIndex.get(i) >= inStartIndex.get(j) &&
						paramEndIndex.get(i) <= inEndIndex.get(j)){
					parameters.get(i).setConditionType(ConditionType.In);
					break;
				}
			}
		}
	}
	
	public static void rebuildCSharpInClauseSQL(String sql, List<CSharpParameterHost> params){
		
	}
	
	public static void main(String[] args) throws Exception{
		String sql = "SELECT [Birth],[Name],[Age],[ID] FROM [PerformanceTest].[dbo].[Person] WITH (NOLOCK) ORDER BY Age asc";
		String cet = pagingQuerySql(sql, DatabaseCategory.SqlServer, CurrentLanguage.Java);
		sql = "SELECT `Birth` FROM Person WHERE  `ID` In (?) and id = ? and name like ? and id in(?)";
		rebuildJavaInClauseSQL(sql, null);
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
