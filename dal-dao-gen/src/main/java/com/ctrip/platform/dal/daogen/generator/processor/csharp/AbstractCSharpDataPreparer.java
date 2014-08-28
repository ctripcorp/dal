package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;

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
			GenTaskByTableViewSp tableViewSp,
			String table, DatabaseCategory dbCategory,
			List<StoredProcedure> allSpNames) throws Exception {

		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		if (!DbUtils.tableExists(tableViewSp.getDb_name(), table)) {
			throw new Exception(String.format("表 %s 不存在，请编辑DAO再生成", table));
		}

		// 主键及所有列
		List<AbstractParameterHost> allColumnsAbstract = DbUtils
				.getAllColumnNames(tableViewSp.getDb_name(), table,
						CurrentLanguage.CSharp);

		List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
				tableViewSp.getDb_name(), table);

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

		Queue<GenTaskBySqlBuilder> _sqlBuilders = ctx.get_sqlBuilders();
		List<GenTaskBySqlBuilder> currentTableBuilders = filterExtraMethods(
				_sqlBuilders, tableViewSp.getDb_name(), table);

		List<CSharpMethodHost> methods = buildMethodHosts(allColumns,
				currentTableBuilders);

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
					tableViewSp.getDb_name(), table, allSpNames, "i"));
			tableHost.setSpaUpdate(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getDb_name(), table, allSpNames, "u"));
			tableHost.setSpaDelete(CSharpSpaOperationHost.getSpaOperation(
					tableViewSp.getDb_name(), table, allSpNames, "d"));
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
		tableHost.setHasSpt(tableHost.isHasSptI() || tableHost.isHasSptU()
				|| tableHost.isHasSptD());

		return tableHost;
	}
	
	private List<GenTaskBySqlBuilder> filterExtraMethods(
			Queue<GenTaskBySqlBuilder> sqlBuilders, String dbName, String table) {
		List<GenTaskBySqlBuilder> currentTableBuilders = new ArrayList<GenTaskBySqlBuilder>();

		Iterator<GenTaskBySqlBuilder> iter = sqlBuilders.iterator();
		while (iter.hasNext()) {
			GenTaskBySqlBuilder currentSqlBuilder = iter.next();
			if (currentSqlBuilder.getDb_name().equals(dbName)
					&& currentSqlBuilder.getTable_name().equals(table)) {
				currentTableBuilders.add(currentSqlBuilder);
				iter.remove();
			}
		}

		return currentTableBuilders;
	}
	
	private List<CSharpMethodHost> buildMethodHosts(
			List<CSharpParameterHost> allColumns,
			List<GenTaskBySqlBuilder> currentTableBuilders) {
		List<CSharpMethodHost> methods = new ArrayList<CSharpMethodHost>();

		for (GenTaskBySqlBuilder builder : currentTableBuilders) {
			CSharpMethodHost method = new CSharpMethodHost();
			method.setCrud_type(builder.getCrud_type());
			method.setName(builder.getMethod_name());
			
			Matcher m = CSharpCodeGenContext.inRegxPattern.matcher(builder.getSql_content());
			int index = 0;
			String temp=builder.getSql_content();
			while(m.find())
	    	{
				temp = temp.replace(m.group(1), String.format("({%d}) ", index));
				index ++;
	    	}
			method.setSql(temp);
			method.setScalarType(builder.getScalarType());
			method.setPaging(builder.isPagination());
			
			List<CSharpParameterHost> parameters = new ArrayList<CSharpParameterHost>();
			if (method.getCrud_type().equals("select")
					|| method.getCrud_type().equals("delete")) {
				if(method.getCrud_type().equals("select")){
					List<AbstractParameterHost> paramAbstractHosts = 
					DbUtils.getSelectFieldHosts(builder.getDb_name(), builder.getSql_content(), CurrentLanguage.Java);
					List<JavaParameterHost> paramHosts = new ArrayList<JavaParameterHost>();
					for (AbstractParameterHost phost : paramAbstractHosts) {
						paramHosts.add((JavaParameterHost)phost);
					}
					method.setFields(paramHosts);
				}
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				
				for (String condition : conditions) {
					String[] tokens = StringUtils.split(condition, ",");
					String name = tokens[0];
					int type = tokens.length >= 2 ? Integer.parseInt(tokens[1])
							: -1;
					String alias = "";
					if (tokens.length >= 3)
						alias = tokens[2];
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							host_al.setInParameter(ConditionType.In == ConditionType
									.valueOf(type));
							parameters.add(host_al);
							// Between need an extra parameter
							if (ConditionType.Between == ConditionType
									.valueOf(type)) {
								CSharpParameterHost host_bw = new CSharpParameterHost(
										pHost);
								String alias_bw = tokens.length >= 4 ? tokens[3]
										: "";
								host_bw.setAlias(alias_bw);
								parameters.add(host_bw);
							}
							break;
						}
					}
				}
			} else if (method.getCrud_type().equals("insert")) {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				for (String field : fields) {
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
			} else {
				String[] fields = StringUtils.split(builder.getFields(), ",");
				String[] conditions = StringUtils.split(builder.getCondition(),
						";");
				for (String field : fields) {
					for (CSharpParameterHost pHost : allColumns) {
						if (pHost.getName().equals(field)) {
							parameters.add(pHost);
							break;
						}
					}
				}
				for (String condition : conditions) {
					for (CSharpParameterHost pHost : allColumns) {
						String[] tokens = StringUtils.split(condition, ",");
						String name = tokens[0];
						int type = tokens.length >= 2 ? Integer.parseInt(tokens[1])
								: -1;
						String alias = "";
						if (tokens.length >= 3)
							alias = tokens[2];
						if (pHost.getName().equals(name)) {
							CSharpParameterHost host_al = new CSharpParameterHost(
									pHost);
							host_al.setAlias(alias);
							parameters.add(host_al);
							if (ConditionType.Between == ConditionType
									.valueOf(type)) {
								CSharpParameterHost host_bw = new CSharpParameterHost(
										pHost);
								String alias_bw = tokens.length >= 4 ? tokens[3]
										: "";
								host_bw.setAlias(alias_bw);
								parameters.add(host_bw);
							}
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
}
