package com.ctrip.platform.dal.common.enums;

import java.util.Set;
import java.util.TreeSet;

import com.ctrip.platform.dal.dao.markdown.ErrorContext;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public enum DatabaseCategory {
	MySql(
			"SELECT %s FROM %s WHERE %s",
			"SELECT %s FROM %s WHERE %s LIMIT %d",
			"SELECT %s FROM %s WHERE %s LIMIT %d, %d",
			" limit %d, %d",
			"%s=IFNULL(?,%s) ",
			"CURRENT_TIMESTAMP",
			new int[]{1043,1159,1161}, 
			new int[]{1021,1037,1038,1039,1040,1041,1154,1158,1160,1189,1190,1205,1218,1219,1220}
			){
		public String quote(String fieldName){
			return "[" + fieldName + "]";
		}
		
		public boolean isTimeOutException(ErrorContext ctx){
			return ctx.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString());
		}

	},
			
	SqlServer(
			"SELECT %s FROM %s WITH (NOLOCK) WHERE %s",
			"SELECT TOP %d %s FROM %s WITH (NOLOCK) WHERE %s",
			"SELECT %s FROM %s WITH (NOLOCK) WHERE %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY",
			" OFFSET %d ROWS FETCH NEXT %d ROWS ONLY",
			"%s=ISNULL(?,%s) ",
			"getDate()",
			new int[]{-2,233,845,846,847,1421},
			new int[]{2,53,701,802,945,1204,1222}
			){
		public String quote(String fieldName){
			return "`" + fieldName + "`";
		}
		
		public boolean isTimeOutException(ErrorContext ctx){
			return ctx.getMsg().startsWith("The query has timed out") || ctx.getMsg().startsWith("查询超时");
		}

	},
	
	Oracle(
			"SELECT %s FROM %s WHERE %s",
			"SELECT %s FROM %s WHERE %s LIMIT %d",
			"SELECT %s FROM %s WHERE %s LIMIT %d, %d",
			" limit %d, %d",
			"%s=IFNULL(?,%s) ",
			"CURRENT_TIMESTAMP",
			new int[]{1043,1159,1161}, 
			new int[]{1021,1037,1038,1039,1040,1041,1154,1158,1160,1189,1190,1205,1218,1219,1220}
			){
		public String quote(String fieldName){
			return fieldName;
		}
		
		public boolean isTimeOutException(ErrorContext ctx){
			return false;
		}

	};
	
	private String queryTpl;
	private String queryTopTpl;
	private String queryByPageTpl;
	private String pageSuffixTpl;
	private String nullableUpdateTpl;
	private String timestampExp;
	private Set<Integer> retriableCodeSet;
	private Set<Integer> failOverableCodeSet;
	
	public static final String SQL_PROVIDER = "sqlProvider";
	public static final String MYSQL_PROVIDER = "mySqlProvider";
	public static final String ORACLE_PROVIDER = "oracleProvider";
	
	public static DatabaseCategory matchWith(String provider) {
		if(provider == null)
			throw new RuntimeException("The provider value can not be NULL");
		
		if(provider.equals(SQL_PROVIDER)) {
			return DatabaseCategory.SqlServer;
		}
		
		if(provider.equals(MYSQL_PROVIDER)) {
			return DatabaseCategory.MySql;
		}
		
		throw new RuntimeException("The provider: " + provider + " can not be recoganized");
	}
	
	public Set<Integer> getDefaultRetriableErrorCodes() {
		return new TreeSet<Integer>(retriableCodeSet);
	}

	public Set<Integer> getDefaultFailOverableErrorCodes() {
		return new TreeSet<Integer>(failOverableCodeSet);
	}
	
	public Set<Integer> getDefaultErrorCodes() {
		Set<Integer> errorCodes = getDefaultRetriableErrorCodes();
		errorCodes.addAll(retriableCodeSet);
		return errorCodes;
	}
	
	public String getPageSuffixTpl() {
		return pageSuffixTpl;
	}

	public String getTimestampExp() {
		return timestampExp;
	}

	public abstract boolean isTimeOutException(ErrorContext ctx);
	
	public abstract String quote(String fieldName);
	
	public String buildTop(String effectiveTableName, String columns, String whereExp, int count){
		switch (this) {
		case SqlServer:
			return String.format(queryTopTpl, count, columns, effectiveTableName, whereExp);
		case MySql:
			return String.format(queryTopTpl, columns, effectiveTableName, whereExp, count);
		default:
			return "";
		}
	}
	
	public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count){
		switch (this) {
		case SqlServer:
		case MySql:
			return String.format(queryByPageTpl, columns, effectiveTableName, whereExp, start, count);
		default:
			return "";
		}
	}

	public String buildList(String effectiveTableName, String columns, String whereExp){
		switch (this) {
		case SqlServer:
		case MySql:
			return String.format(queryTpl, columns, effectiveTableName, whereExp);
		default:
			return "";
		}
	}

	public String getNullableUpdateTpl() {
		return nullableUpdateTpl;
	}

	private DatabaseCategory(String queryTpl, String topSqlTpl, String pageSqlTpl, String pageSuffixTpl, String nullableUpdateTpl, String timestampExp, int[] retriableCodes, int[] failOverableCodes) {
		this.queryTpl = queryTpl;
		this.queryTopTpl = topSqlTpl;
		this.queryByPageTpl = pageSqlTpl;
		this.pageSuffixTpl = pageSuffixTpl;
		this.nullableUpdateTpl = nullableUpdateTpl;
		this.timestampExp = timestampExp;
		this.retriableCodeSet = parseErrorCodes(retriableCodes);
		this.failOverableCodeSet = parseErrorCodes(failOverableCodes);
	}
	
	private Set<Integer> parseErrorCodes(int[] codes){
		Set<Integer> temp = new TreeSet<Integer>();
		for(int value: codes)
			temp.add(value);

		return temp;
	}
}
