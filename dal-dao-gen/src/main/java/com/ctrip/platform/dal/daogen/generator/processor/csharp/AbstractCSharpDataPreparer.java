package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.domain.StoredProcedure;
import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpMethodHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpSpaOperationHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;

public class AbstractCSharpDataPreparer{
	
	private static DaoOfDatabaseSet daoOfDatabaseSet;
	
	static {
		daoOfDatabaseSet = SpringBeanGetter.getDaoOfDatabaseSet();
	}

	protected void addDatabaseSet(CodeGenContext codeGenCtx, String databaseSetName){
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
		if(null == sets || sets.isEmpty()){
//			log.error(String.format("The databaseSet name[%s] does not exist", databaseSetName));
			return;
		}
		DalConfigHost dalConfigHost = ctx.getDalConfigHost();
		dalConfigHost.addDatabaseSet(sets);
		for (DatabaseSet databaseSet : sets) {
			List<DatabaseSetEntry> entries = daoOfDatabaseSet.getAllDatabaseSetEntryByDbsetid(databaseSet.getId());
			if(null == entries || entries.isEmpty()){
//				log.error(String.format("The databaseSet[%s] does't contain any entries", databaseSet.getId()));
				continue;
			}
			dalConfigHost.addDatabaseSetEntry(entries);
		}
	} 
	
	protected CSharpTableHost buildTableHost(CodeGenContext codeGenCtx, 
			GenTaskByTableViewSp tableViewSp, String table, DatabaseCategory dbCategory,
			List<StoredProcedure> allSpNames) throws Exception {

		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		if (!DbUtils.tableExists(tableViewSp.getAllInOneName(), table)) {
			throw new Exception(String.format("表 %s 不存在，请编辑DAO再生成", table));
		}

		// 主键及所有列
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getAllInOneName(), table,
						CurrentLanguage.CSharp);

		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getAllInOneName(), table);

		List<CSharpParameterHost> allColumns = new ArrayList<CSharpParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((CSharpParameterHost) h);
		}

		List<CSharpParameterHost> primaryKeys = new ArrayList<CSharpParameterHost>();
		for (CSharpParameterHost h : allColumns) {
			if (primaryKeyNames.contains(h.getName())) {
				h.setPrimary(true);
				primaryKeys.add(h);
			}
		}

		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.getSqlBuilders();
		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(
				_sqlBuilders, tableViewSp.getAllInOneName(), table);

		List<CSharpMethodHost> methods = buildSqlBuilderMethodHost(allColumns, currentTableBuilders);
		
		CSharpTableHost tableHost = new CSharpTableHost();
		tableHost.setExtraMethods(methods);
		tableHost.setNameSpace(ctx.getNamespace());
		tableHost.setDatabaseCategory(dbCategory);
		tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
		tableHost.setTableName(table);
		tableHost.setClassName(CommonUtils.normalizeVariable(getPojoClassName(
				tableViewSp.getPrefix(), tableViewSp.getSuffix(), table)));
		tableHost.setTable(true);
		tableHost.setSpa(tableViewSp.isCud_by_sp());
		// SP方式增删改
		if (tableHost.isSpa()) {
			tableHost.setSpaInsert(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getAllInOneName(), table, allSpNames, "i"));
			tableHost.setSpaUpdate(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getAllInOneName(), table, allSpNames, "u"));
			tableHost.setSpaDelete(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getAllInOneName(), table, allSpNames, "d"));
		}

		tableHost.setPrimaryKeys(primaryKeys);
		tableHost.setColumns(allColumns);

		tableHost.setHasPagination(tableViewSp.isPagination());

		StoredProcedure expectSptI = new StoredProcedure();
		expectSptI.setName(String.format("spT_%s_i", table));

		StoredProcedure expectSptU = new StoredProcedure();
		expectSptU.setName(String.format("spT_%s_u", table));

		StoredProcedure expectSptD = new StoredProcedure();
		expectSptD.setName(String.format("spT_%s_d", table));

		tableHost.setHasSptI(allSpNames.contains(expectSptI));
		tableHost.setHasSptU(allSpNames.contains(expectSptU));
		tableHost.setHasSptD(allSpNames.contains(expectSptD));
		tableHost.setHasSpt(tableHost.isHasSptI() || tableHost.isHasSptU() || tableHost.isHasSptD());
		
		tableHost.setApi_list(tableViewSp.getApi_list());

		return tableHost;
	}
	
	private List<GenTaskBySqlBuilder> filterExtraMethods(Queue<GenTaskBySqlBuilder> sqlBuilders, 
			String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
		while (iter.hasNext()) {
			GenTaskBySqlBuilder currentSqlBuilder = iter.next();
			if (currentSqlBuilder.getAllInOneName().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(table)) {
				currentTableBuilders.add(currentSqlBuilder);
				iter.remove();
			}
		}

		return currentTableBuilders;
	}
	
	private List<CSharpMethodHost> buildSqlBuilderMethodHost(List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
		methods.addAll(buildSelectMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildDeleteMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildInsertMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildUpdateMethodHosts(allColumns,currentTableBuilders));
		return methods;
	}
	
	private List<CSharpMethodHost> buildSelectMethodHosts(List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("select")) {
				continue;
			}
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			String sql = builder.getSql_content();
			int index = 0;
			if(builder.isPagination()){
				sql = SqlBuilder.pagingQuerySql(sql, getDatabaseCategory(builder.getAllInOneName()), CurrentLanguage.CSharp);
				index += 2;
			}
			Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(builder.getSql_content());
			while(m.find()){
				sql = sql.replace(m.group(1), String.format("({%d}) ", index));
				index++;
	    	}
			method.setSql(sql);
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			
			List<AbstractParameterHost> paramAbstractHosts = 
					DbUtils.getSelectFieldHosts(builder.getAllInOneName(), builder.getSql_content(), 
							CurrentLanguage.Java);
			List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
			for (AbstractParameterHost phost : paramAbstractHosts) {
				paramHosts.add((JavaParameterHost)phost);
			}
			method.setFields(paramHosts);
			
			List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);
			method.setParameters(buildSqlParamName(whereParams, method.getSql()));
			
			String orderBy = builder.getOrderby();
			if(orderBy!=null && !orderBy.trim().isEmpty() && orderBy.indexOf("-1,")!=0){
				String []str = orderBy.split(",");
				String odyExp = "p => p."+str[0]+", ";
				odyExp = "asc".equalsIgnoreCase(str[1])?odyExp+"true":odyExp+"false";
				method.setOrderByExp(odyExp);
			}
			methods.add(method);
		}
		return methods;
	}
	
	private List<CSharpParameterHost> buildSqlParamName(List<CSharpParameterHost> whereParams, String sql){
		Pattern ptn = Pattern.compile("@([^\\s]+)", Pattern.CASE_INSENSITIVE);
		Matcher mt = ptn.matcher(sql);
		Queue<String> sqlParamQueue = new LinkedList<String>();
		while(mt.find()){
			sqlParamQueue.add(mt.group(1));
		}
		for(CSharpParameterHost param : whereParams){
			String sqlParamName = sqlParamQueue.poll();
			if(sqlParamName==null)
				sqlParamName = param.getAlias();
			param.setSqlParamName(sqlParamName);
		}
		return whereParams;
	}
	
	private List<CSharpMethodHost> buildDeleteMethodHosts(List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("delete")) {
				continue;
			}
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			
			List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);
			method.setParameters(buildSqlParamName(whereParams, method.getSql()));
			methods.add(method);
		}
		return methods;
	}
	
	private List<CSharpMethodHost> buildInsertMethodHosts(List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("insert")) {
				continue;
			}
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			
			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
			if (method.getCrud_type().equals("insert")) {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				for (String field : fields) {
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			}
			method.setParameters(parameters);
			methods.add(method);
		}
		return methods;
	}
	
	private List<CSharpMethodHost> buildUpdateMethodHosts(List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("update")) {
				continue;
			}
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			
			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
			
			String[] fields = StringUtils.split(builder.getFields(), ",");
			for (String field : fields) {
				for (CSharpParameterHost pHost : allColumns) {
					if (pHost.getName().equals(field)) {
						parameters.add(pHost);
						break;
					}
				}
			}
			List<CSharpParameterHost> whereParams = buildMethodParameterHost4SqlConditin(builder, allColumns);
			method.setParameters(buildSqlParamName(whereParams, method.getSql()));
			
			method.setParameters(parameters);
			methods.add(method);
		}
		return methods;
	}
	
	private List<CSharpParameterHost> buildMethodParameterHost4SqlConditin(GenTaskBySqlBuilder builder,
			List<CSharpParameterHost> allColumns){
		List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
		String[] conditions = StringUtils.split(builder.getCondition(), ";");
		for (String condition : conditions) {
			String[] tokens = StringUtils.split(condition, ",");
			String name = tokens[0];
			int type = tokens.length >= 2 ? Integer.parseInt(tokens[1]) : -1;
			String alias = tokens.length >= 3 ? tokens[2] : "";
			for (CSharpParameterHost pHost : allColumns) {
				if (pHost.getName().equals(name)) {
					CSharpParameterHost host_al = new CSharpParameterHost(pHost);
					host_al.setAlias(alias);
					host_al.setInParameter(ConditionType.In == ConditionType.valueOf(type));
					if (-1 != type)
						host_al.setConditionType(ConditionType.valueOf(type));
					parameters.add(host_al);
					// Between need an extra parameter
					if (ConditionType.Between == ConditionType.valueOf(type)) {
						CSharpParameterHost host_bw = new CSharpParameterHost(pHost);
						String alias_bw = tokens.length >= 4 ? tokens[3] : "";
						host_bw.setAlias(alias_bw);
						host_bw.setConditionType(ConditionType.Between);
						parameters.add(host_bw);
						boolean nullable = tokens.length >= 5?Boolean.valueOf(tokens[4]):false;
						host_al.setNullable(nullable);
						host_bw.setNullable(nullable);
					}else{
						boolean nullable = tokens.length >= 4?Boolean.valueOf(tokens[3]):false;
						host_al.setNullable(nullable);
					}
					break;
				}
			}
		}		
		return parameters;
	}
	
	protected String getPojoClassName(String prefix, String suffix, String table) {
		String className = table;
		if (null != prefix && !prefix.isEmpty()) {
			className = className.substring(prefix.length());
		}
		if (null != suffix && !suffix.isEmpty()) {
			className = className + WordUtils.capitalize(suffix);
		}

		StringBuilder result = new StringBuilder();
		for (String str : StringUtils.split(className, "_")) {
			result.append(WordUtils.capitalize(str));
		}

		return WordUtils.capitalize(result.toString());
	}

	protected DatabaseCategory getDatabaseCategory(String dbName) throws Exception {
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		String dbType = DbUtils.getDbType(dbName);
		if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}
		return dbCategory;
	}
	
//	private List<CSharpMethodHost> buildMethodHosts(List<CSharpParameterHost> allColumns,
//			List<GenTaskBySqlBuilder> currentTableBuilders) throws Exception {
//		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();
//
//		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
//			CSharpMethodHost method = new CSharpMethodHost();
//			method.setCrud_type(builder.getCrud_type());
//			method.setName(builder.getMethod_name());
//			String sql = builder.getSql_content();
//			int index = 0;
//			if(builder.isPagination()){
//				sql = SqlBuilder.pagingQuerySql(sql, getDatabaseCategory(builder.getAllInOneName()), CurrentLanguage.CSharp);
//				index += 2;
//			}
//			Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(builder.getSql_content());
//			while(m.find()){
//				sql = sql.replace(m.group(1), String.format("({%d}) ", index));
//				index++;
//	    	}
//			method.setSql(sql);
//			method.setScalarType(builder.getScalarType());
//			method.setPaging(builder.isPagination());
//			
//			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
//			if (method.getCrud_type().equals("select") || method.getCrud_type().equals("delete")) {
//				if(method.getCrud_type().equals("select")) {
//					List<AbstractParameterHost> paramAbstractHosts = 
//							DbUtils.getSelectFieldHosts(builder.getAllInOneName(), builder.getSql_content(), 
//									CurrentLanguage.Java);
//					List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
//					for (AbstractParameterHost phost : paramAbstractHosts) {
//						paramHosts.add((JavaParameterHost)phost);
//					}
//					method.setFields(paramHosts);
//				}
//				String[] conditions = StringUtils.split(builder.getCondition(), ";");
//				
//				for (String condition : conditions) {
//					String[] tokens = StringUtils.split(condition, ",");
//					String name = tokens[0];
//					int type = tokens.length >= 2 ? Integer.parseInt(tokens[1]) : -1;
//					String alias = "";
//					if (tokens.length >= 3)
//						alias = tokens[2];
//					for (CSharpParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(name)) {
//							CSharpParameterHost host_al = new CSharpParameterHost(pHost);
//							host_al.setAlias(alias);
//							host_al.setInParameter(ConditionType.In == ConditionType.valueOf(type));
//							parameters.add(host_al);
//							// Between need an extra parameter
//							if (ConditionType.Between == ConditionType.valueOf(type)) {
//								CSharpParameterHost host_bw = new CSharpParameterHost(pHost);
//								String alias_bw = tokens.length >= 4 ? tokens[3] : "";
//								host_bw.setAlias(alias_bw);
//								parameters.add(host_bw);
//							}
//							break;
//						}
//					}
//				}
//			} else if (method.getCrud_type().equals("insert")) {
//				String[] fields = StringUtils.split(builder.getFields(), ",");
//				for (String field : fields) {
//					for (CSharpParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(field)) {
//							parameters.add(pHost);
//							break;
//						}
//					}
//				}
//			} else {
//				String[] fields = StringUtils.split(builder.getFields(), ",");
//				String[] conditions = StringUtils.split(builder.getCondition(), ";");
//				for (String field : fields) {
//					for (CSharpParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(field)) {
//							parameters.add(pHost);
//							break;
//						}
//					}
//				}
//				for (String condition : conditions) {
//					for (CSharpParameterHost pHost : allColumns) {
//						String[] tokens = StringUtils.split(condition, ",");
//						String name = tokens[0];
//						int type = tokens.length >= 2 ? Integer.parseInt(tokens[1]) : -1;
//						String alias = "";
//						if (tokens.length >= 3)
//							alias = tokens[2];
//						if (pHost.getName().equals(name)) {
//							CSharpParameterHost host_al = new CSharpParameterHost(pHost);
//							host_al.setAlias(alias);
//							host_al.setInParameter(ConditionType.In == ConditionType
//									.valueOf(type));
//							parameters.add(host_al);
//							if (ConditionType.Between == ConditionType.valueOf(type)) {
//								CSharpParameterHost host_bw = new CSharpParameterHost(pHost);
//								String alias_bw = tokens.length >= 4 ? tokens[3] : "";
//								host_bw.setAlias(alias_bw);
//								parameters.add(host_bw);
//							}
//							break;
//						}
//					}
//				}
//			}
//			method.setParameters(parameters);
//			methods.add(method);
//		}
//		return methods;
//	}
}
