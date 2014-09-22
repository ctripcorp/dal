package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

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
import com.ctrip.platform.dal.daogen.entity.Resource;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.host.java.ContextHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.host.java.SpOperationHost;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class AbstractJavaDataPreparer{

	protected void addDatabaseSet(CodeGenContext codeGenCtx, String databaseSetName){
		DaoOfDatabaseSet daoOfDatabaseSet = SpringBeanGetter.getDaoOfDatabaseSet();
		List<DatabaseSet> sets = daoOfDatabaseSet.getAllDatabaseSetByName(databaseSetName);
		if(null == sets || sets.isEmpty()){
//			log.error(String.format("The databaseSet name[%s] does not exist", databaseSetName));
			return;
		}
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		DalConfigHost dalConfigHost = ctx.getDalConfigHost();
		ContextHost contextHost = ctx.getContextHost();
		dalConfigHost.addDatabaseSet(sets);
		for (DatabaseSet databaseSet : sets) {
			List<DatabaseSetEntry> entries = daoOfDatabaseSet.getAllDatabaseSetEntryByDbsetid(databaseSet.getId());
			if(null == entries || entries.isEmpty()){
//				log.error(String.format("The databaseSet[%s] does't contain any entries", databaseSet.getId()));
				continue;
			}
			dalConfigHost.addDatabaseSetEntry(entries);
			
			for (DatabaseSetEntry entry : entries) {
				contextHost.addResource(new Resource(entry.getConnectionString()));
			}
		}
	}
	
	protected JavaTableHost buildTableHost(CodeGenContext codeGenCtx, GenTaskByTableViewSp tableViewSp,
			String tableName) throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		if(!DbUtils.tableExists(tableViewSp.getAllInOneName(), tableName)){
			throw new Exception(String.format("The table doesn't exist, pls check", tableViewSp.getAllInOneName(), tableName));
		}
		JavaTableHost tableHost = new JavaTableHost();
		tableHost.setPackageName(ctx.getNamespace());
		tableHost.setDatabaseCategory(getDatabaseCategory(tableViewSp.getAllInOneName()));
		tableHost.setDbSetName(tableViewSp.getDatabaseSetName());
		tableHost.setTableName(tableName);
		tableHost.setPojoClassName(getPojoClassName(tableViewSp.getPrefix(),
				tableViewSp.getSuffix(), tableName));
		tableHost.setSp(tableViewSp.isCud_by_sp());
		tableHost.setApi_list(tableViewSp.getApi_list());

		// 主键及所有列
		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getAllInOneName(), tableName);
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getAllInOneName(), tableName,
						CurrentLanguage.Java);
		if(null == allColumnsAbstract){
			throw new Exception(String.format("The column names of tabel[%s, %s] is null", 
					tableViewSp.getAllInOneName(), tableName));
		}
		List<JavaParameterHost> allColumns = new ArrayList<JavaParameterHost>();
		for (AbstractParameterHost h : allColumnsAbstract) {
			allColumns.add((JavaParameterHost) h);
		}

		List<JavaParameterHost> primaryKeys = new ArrayList<JavaParameterHost>();
		boolean hasIdentity = false;
		String identityColumnName = null;
		for (JavaParameterHost h : allColumns) {
			if (!hasIdentity && h.isIdentity()) {
				hasIdentity = true;
				identityColumnName = h.getName();
			}
			if (primaryKeyNames.contains(h.getName())) {
				h.setPrimary(true);
				primaryKeys.add(h);
			}
		}

		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(ctx, tableViewSp.getAllInOneName(), tableName);

		List<JavaMethodHost> methods = buildSqlBuilderMethodHost(allColumns, currentTableBuilders);

		tableHost.setFields(allColumns);
		tableHost.setPrimaryKeys(primaryKeys);
		tableHost.setHasIdentity(hasIdentity);
		tableHost.setIdentityColumnName(identityColumnName);
		tableHost.setMethods(methods);

		if (tableHost.isSp()) {
			tableHost.setSpInsert(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "i"));
			tableHost.setSpUpdate(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "u"));
			tableHost.setSpDelete(getSpaOperation(tableViewSp.getAllInOneName(), tableName, "d"));
		}
		return tableHost;
	}
	
	protected DatabaseCategory getDatabaseCategory(String dbName) throws Exception {
		DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
		String dbType = DbUtils.getDbType(dbName);
		if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
			dbCategory = DatabaseCategory.MySql;
		}
		return dbCategory;
	}
	
	protected String getPojoClassName(String prefix, String suffix, String tableName) {
		String className = tableName;
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
	
	private List<GenTaskBySqlBuilder> filterExtraMethods(CodeGenContext codeGenCtx, String dbName, 
			String tableName) {
		
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.getSqlBuilders();
		
		Iterator<GenTaskBySqlBuilder> iter = _sqlBuilders.iterator();
		while (iter.hasNext()) {
			GenTaskBySqlBuilder currentSqlBuilder = iter.next();
			if (currentSqlBuilder.getAllInOneName().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(tableName)) {
				currentTableBuilders.add(currentSqlBuilder);
				iter.remove();
			}
		}

		return currentTableBuilders;
	}
	
	private SpOperationHost getSpaOperation(String dbName, String tableName,
			String operation) throws Exception {
		List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(dbName);
		return SpOperationHost.getSpaOperation(dbName, tableName, allSpNames, operation);
	}
	
	private List<JavaMethodHost> buildSqlBuilderMethodHost(List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
		methods.addAll(buildSelectMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildDeleteMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildInsertMethodHosts(allColumns,currentTableBuilders));
		methods.addAll(buildUpdateMethodHosts(allColumns,currentTableBuilders));
		return methods;
	}
	
	private List<JavaMethodHost> buildSelectMethodHosts(List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("select")) {
				continue;
			}
			JavaMethodHost method = new JavaMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			method.setComments(builder.getComment());
			// select sql have select field and where condition clause
			List<AbstractParameterHost> paramAbstractHosts = 
					DbUtils.getSelectFieldHosts(builder.getAllInOneName(), builder.getSql_content(), CurrentLanguage.Java);
			List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
			for (AbstractParameterHost phost : paramAbstractHosts) {
				paramHosts.add((JavaParameterHost)phost);
			}
			method.setFields(paramHosts);
			
			method.setParameters(buildMethodParameterHost4SqlConditin(builder, allColumns));
			methods.add(method);
		}
		return methods;
	}
	
	private List<JavaMethodHost> buildDeleteMethodHosts(List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("delete")) {
				continue;
			}
			JavaMethodHost method = new JavaMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			method.setComments(builder.getComment());
			// Only have condition clause
			method.setParameters(buildMethodParameterHost4SqlConditin(builder, allColumns));
			methods.add(method);
		}
		return methods;
	}

	private List<JavaMethodHost> buildInsertMethodHosts(List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("insert")) {
				continue;
			}
			JavaMethodHost method = new JavaMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			method.setComments(builder.getComment());
			List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
			
			// Have no where condition
			String[] fields = StringUtils.split(builder.getFields(), ",");
			for (String field : fields) {
				for (JavaParameterHost pHost : allColumns) {
					if (pHost.getName().equals(field)) {
						parameters.add(pHost);
						break;
					}
				}
			}
			
			method.setParameters(parameters);
			methods.add(method);
		}
		return methods;
	}
	
	private List<JavaMethodHost> buildUpdateMethodHosts(List<JavaParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			if (!builder.getCrud_type().equals("update")) {
				continue;
			}
			JavaMethodHost method = new JavaMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			method.setSql(builder.getSql_content());
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			method.setComments(builder.getComment());
			List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
			// Have both set and condition clause
			String[] fields = StringUtils.split(builder.getFields(), ",");
			for (String field : fields) {
				for (JavaParameterHost pHost : allColumns) {
					if (pHost.getName().equals(field)) {
						JavaParameterHost host_ls = new JavaParameterHost(pHost);
						parameters.add(host_ls);
						break;
					}
				}
			}
			parameters.addAll(buildMethodParameterHost4SqlConditin(builder, allColumns));
			method.setParameters(parameters);
			methods.add(method);
		}
		return methods;
	}
	
	private List<JavaParameterHost> buildMethodParameterHost4SqlConditin(GenTaskBySqlBuilder builder, 
			List<JavaParameterHost> allColumns){
		List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
		String[] conditions = StringUtils.split(builder.getCondition(), ";");
		for (String condition : conditions) {
			String[] tokens = StringUtils.split(condition, ",");
			String name = tokens[0];
			int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
			if(type == 9 || type == 10){ //is null、is not null don't hava param
				continue;
			}
			String alias = tokens.length >= 3 ? tokens[2] : "";
			for (JavaParameterHost pHost : allColumns) {
				if (pHost.getName().equals(name)) {
					JavaParameterHost host_ls = new JavaParameterHost(pHost);
					host_ls.setAlias(alias);
					host_ls.setConditional(true);
					if (-1 != type)
						host_ls.setConditionType(ConditionType.valueOf(type));
					parameters.add(host_ls);
					// Between need an extra parameter
					if (ConditionType.Between == host_ls.getConditionType()) {
						JavaParameterHost host_bw = new JavaParameterHost(host_ls);
						String alias_bw = tokens.length >= 4 ? tokens[3] : "";
						host_bw.setAlias(alias_bw);
						parameters.add(host_bw);
					}
					break;
				}
			}
		}
		return parameters;
	}
	
//	private List<JavaMethodHost> buildMethodHosts(
//			List<JavaParameterHost> allColumns,
//			List<GenTaskBySqlBuilder> currentTableBuilders) {
//		List<JavaMethodHost> methods = new ArrayList<JavaMethodHost>();
//
//		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
//			JavaMethodHost method = new JavaMethodHost();
//			method.setCrud_type(builder.getCrud_type());
//			method.setName(builder.getMethod_name());
//			method.setSql(builder.getSql_content());
//			method.setScalarType(builder.getScalarType());
//			method.setPaging(builder.isPagination());
//			method.setComments(builder.getComment());
//			List<JavaParameterHost> parameters = new ArrayList<JavaParameterHost>();
//			// Only have condition clause
//			if (method.getCrud_type().equals("select")
//					|| method.getCrud_type().equals("delete")) {
//				
//				if(method.getCrud_type().equals("select")){
//					List<AbstractParameterHost> paramAbstractHosts = 
//							DbUtils.getSelectFieldHosts(builder.getAllInOneName(), builder.getSql_content(), CurrentLanguage.Java);
//					List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
//					for (AbstractParameterHost phost : paramAbstractHosts) {
//						paramHosts.add((JavaParameterHost)phost);
//					}
//					method.setFields(paramHosts);
//				}
//				
//				String[] conditions = StringUtils.split(builder.getCondition(), ";");
//				for (String condition : conditions) {
//					String[] tokens = StringUtils.split(condition, ",");
//					String name = tokens[0];
//					int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
//					String alias = tokens.length >= 3 ? tokens[2] : "";
//					for (JavaParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(name)) {
//							JavaParameterHost host_ls = new JavaParameterHost(pHost);
//							host_ls.setAlias(alias);
//							host_ls.setConditional(true);
//							if (-1 != type)
//								host_ls.setConditionType(ConditionType.valueOf(type));
//							parameters.add(host_ls);
//							// Between need an extra parameter
//							if (ConditionType.Between == host_ls.getConditionType()) {
//								JavaParameterHost host_bw = new JavaParameterHost(host_ls);
//								String alias_bw = tokens.length >= 4 ? tokens[3] : "";
//								host_bw.setAlias(alias_bw);
//								parameters.add(host_bw);
//							}
//							break;
//						}
//					}
//				}
//			}
//			// Have no condition
//			else if (method.getCrud_type().equals("insert")) {
//				String[] fields = StringUtils.split(builder.getFields(), ",");
//				for (String field : fields) {
//					for (JavaParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(field)) {
//							parameters.add(pHost);
//							break;
//						}
//					}
//				}
//			}
//			// Have both set and condition clause
//			else {
//				String[] fields = StringUtils.split(builder.getFields(), ",");
//				String[] conditions = StringUtils.split(builder.getCondition(), ";");
//				for (String field : fields) {
//					for (JavaParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(field)) {
//							JavaParameterHost host_ls = new JavaParameterHost(pHost);
//							parameters.add(host_ls);
//							break;
//						}
//					}
//				}
//
//				for (String condition : conditions) {
//					String[] tokens = StringUtils.split(condition, ",");
//					String name = tokens[0];
//					int type = tokens.length >= 2 ? CommonUtils.tryParse(tokens[1], -1) : -1;
//					String alias = tokens.length >= 3 ? tokens[2] : "";
//					for (JavaParameterHost pHost : allColumns) {
//						if (pHost.getName().equals(name)) {
//							JavaParameterHost host_ls = new JavaParameterHost(pHost);
//							host_ls.setAlias(alias);
//							host_ls.setConditional(true);
//							if (-1 != type)
//								host_ls.setConditionType(ConditionType.valueOf(type));
//							if (ConditionType.In == host_ls.getConditionType()) {
//
//							}
//							parameters.add(host_ls);
//							// Between need an extra parameter
//							if (ConditionType.Between == host_ls.getConditionType()) {
//								JavaParameterHost host_bw = new JavaParameterHost(host_ls);
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
