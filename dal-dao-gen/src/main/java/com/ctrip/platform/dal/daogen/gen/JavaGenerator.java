package com.ctrip.platform.dal.daogen.gen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.FieldMeta;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.Method;
import com.ctrip.platform.dal.daogen.pojo.Parameter;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class JavaGenerator extends AbstractGenerator {

	private JavaGenerator() {

	}

	private static JavaGenerator instance = new JavaGenerator();
	private static DaoOfDbServer dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDBServerDao();
	}

	public static JavaGenerator getInstance() {
		return instance;
	}

	/**
	 * 为自动生成的SQL生成相应的代码，A SQL a function, a table a DAO
	 * 
	 * @param tasks
	 */
//	public void generateByTableView(List<GenTask> tasks) {
//
//		VelocityContext context = new VelocityContext();
//
//		JavaPojoGenHost pojoHost = new JavaPojoGenHost();
//		JavaParserGenHost parserHost = new JavaParserGenHost();
//		pojoHost.setDaoNamespace(namespace);
//
//		// 每个表生成一个DAO文件，可能有潜在的问题，即两个不同的数据库中有相同的表，后期需要修改
//		Map<String, List<GenTask>> groupByTable = groupByTableName(tasks);
//
//		for (Map.Entry<String, List<GenTask>> entry : groupByTable.entrySet()) {
//			String table = entry.getKey();
//			List<GenTask> groupedTasks = entry.getValue();
//			int serverId = -1;
//			if (groupedTasks.size() > 0) {
//				GenTask currentTask = groupedTasks.get(0); 
//				// context.put("database", groupedTasks.get(0).getDb_name());
//				parserHost.setDbName(currentTask.getDb_name());
//				serverId = currentTask.getServer_id();
//			} else {
//				continue;
//			}
//			
//			parserHost.setTableName(table);
//			
//			pojoHost.setClassName(WordUtils.capitalizeFully(table));
//			parserHost.setClassName(String.format("%sDalParser",
//					pojoHost.getClassName()));
//
//			List<FieldMeta> fieldsMeta = getMetaData(
//					serverId, parserHost.getDbName(), table);
//
//			boolean hasIdentity = false;
//			String identityColumn = null;
//			for (FieldMeta meta : fieldsMeta) {
//				if (!hasIdentity && meta.isIdentity()) {
//					hasIdentity = true;
//					identityColumn = meta.getName();
//				}
//				meta.setType(Consts.JavaSqlTypeMap.get(meta.getDbType()
//						.toLowerCase()));
//			}
//			
//			pojoHost.setFields(fieldsMeta);
//			
//			parserHost.setHasIdentity(hasIdentity);
//			parserHost.setIdentityColumnName(identityColumn);
//			
//			context.put("WordUtils", WordUtils.class);
//			context.put("StringUtils", StringUtils.class);
//			context.put("newline", "\n");
//			context.put("tab", "\t");
//
//			context.put("pojoHost", pojoHost);
//			context.put("parserHost", parserHost);
//
//			// putMethods2Velocity(context, fieldsMeta, groupedTasks);
//
//			FileWriter parserWriter = null;
//			FileWriter pojoWriter = null;
//			try {
//				File mavenLikeDir = new File(String.format("gen/%s/java",
//						projectId));
//				FileUtils.forceMkdir(mavenLikeDir);
//
//				parserWriter = new FileWriter(String.format("%s/%s.java",
//						mavenLikeDir.getAbsolutePath(),
//						parserHost.getClassName()));
//
//				pojoWriter = new FileWriter(
//						String.format("%s/%s.java",
//								mavenLikeDir.getAbsolutePath(),
//								pojoHost.getClassName()));
//
//				Velocity.mergeTemplate("templates/Parser.java.tpl", "UTF-8",
//						context, parserWriter);
//				Velocity.mergeTemplate("templates/Pojo.java.tpl", "UTF-8",
//						context, pojoWriter);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				JavaIOUtils.closeWriter(parserWriter);
//				JavaIOUtils.closeWriter(pojoWriter);
//			}
//		}
//	}

//	private void putMethods2Velocity(VelocityContext context,
//			List<FieldMeta> fieldsMeta, List<GenTask> groupedTasks) {
//
//		String primaryKey = "";
//		for (FieldMeta meta : fieldsMeta) {
//			if (meta.isPrimary()) {
//				primaryKey = meta.getName();
//				break;
//			}
//		}
//		context.put("fields", fieldsMeta);
//
//		List<Method> methods = new ArrayList<Method>();
//		List<Method> spMethods = new ArrayList<Method>();
//
//		for (GenTask task : groupedTasks) {
//			GenTaskBySqlBuilder groupedTask = (GenTaskBySqlBuilder) task;
//
//			if (null == groupedTask.getMethod_name()
//					|| groupedTask.getMethod_name().isEmpty()) {
//				continue;
//			}
//
//			Method m = new Method();
//			m.setAction(groupedTask.getCrud_type().toLowerCase());
//			m.setMethodName(groupedTask.getMethod_name());
//			m.setSqlSPName(groupedTask.getSql_content());
//
//			// 查询，或者SQL形式的删除
//			if (m.getAction().equalsIgnoreCase("select")
//					|| (m.getAction().equalsIgnoreCase("delete") && groupedTask
//							.getSql_type().equalsIgnoreCase("sql"))) {
//				m.setParameters(getParametersByCondition(
//						groupedTask.getCondition(), fieldsMeta));
//				methods.add(m);
//			}
//			// SQL形式的更新
//			else if (m.getAction().equalsIgnoreCase("update")
//					&& groupedTask.getSql_type().equalsIgnoreCase("sql")) {
//				List<Parameter> parameters = new ArrayList<Parameter>();
//				parameters.addAll(getParametersByCondition(
//						groupedTask.getCondition(), fieldsMeta));
//				parameters.addAll(getParametersByFields(
//						groupedTask.getFields(), fieldsMeta));
//				m.setParameters(parameters);
//				methods.add(m);
//			}
//			// SP形式的删除
//			else if (m.getAction().equalsIgnoreCase("delete")
//					&& !groupedTask.getSql_type().equalsIgnoreCase("sql")) {
//				List<Parameter> parameters = new ArrayList<Parameter>();
//				Parameter p = new Parameter();
//				p.setName(primaryKey);
//				p.setFieldName(primaryKey);
//				for (FieldMeta field : fieldsMeta) {
//					if (field.getName().equalsIgnoreCase(primaryKey)) {
//						p.setType(field.getType());
//						p.setParamMode("IN");
//						p.setPosition(1);
//						break;
//					}
//				}
//				parameters.add(p);
//				m.setParameters(parameters);
//				spMethods.add(m);
//			}
//			// SQL形式的插入
//			else if (m.getAction().equalsIgnoreCase("insert")
//					&& groupedTask.getSql_type().equalsIgnoreCase("sql")) {
//				m.setParameters(getParametersByFields(groupedTask.getFields(),
//						fieldsMeta));
//				methods.add(m);
//			}
//			// SP形式的插入，SP形式的Update
//			else {
//				List<String> allFields = new ArrayList<String>();
//				for (FieldMeta meta : fieldsMeta) {
//					allFields.add(meta.getName());
//				}
//				List<Parameter> parameters = getParametersByFields(
//						StringUtils.join(allFields.toArray(), ","), fieldsMeta);
//
//				for (Parameter p : parameters) {
//					if (p.getName().equals(primaryKey)) {
//						p.setParamMode("OUT");
//						break;
//					}
//				}
//
//				m.setParameters(parameters);
//				spMethods.add(m);
//			}
//
//		}
//
//		context.put("methods", methods);
//		context.put("sp_methods", spMethods);
//
//	}

	/**
	 * 取出task表中的fields字段（List类型），组装为Parameter数组
	 * 
	 * @param obj
	 * @param fieldTypeMap
	 * @return
	 */
	private List<Parameter> getParametersByFields(String fields,
			List<FieldMeta> fieldsMeta) {
		List<Parameter> parameters = new ArrayList<Parameter>();

		String[] fieldsArray = fields.split(",");
		for (String field : fieldsArray) {
			Parameter p = new Parameter();
			p.setName(field);
			p.setFieldName(field);
			for (FieldMeta meta : fieldsMeta) {
				if (meta.getName().equalsIgnoreCase(field)) {
					p.setType(meta.getType());
					p.setPosition(meta.getPosition());
					break;
				}
			}
			parameters.add(p);
		}

		return parameters;
	}

	/**
	 * 取出task表中的condition字段（Map类型），组装为Parameter数组
	 * 
	 * @param obj
	 * @param fieldTypeMap
	 * @return
	 */
	private List<Parameter> getParametersByCondition(String condition,
			List<FieldMeta> fieldsMeta) {
		List<Parameter> parameters = new ArrayList<Parameter>();

		String[] conditions = condition.split(",");
		for (String con : conditions) {
			String[] keyValue = con.split("_");
			if (keyValue.length != 2) {
				continue;
			}
			Parameter p = new Parameter();
			p.setName(keyValue[0]);
			p.setFieldName(p.getName());
			for (FieldMeta meta : fieldsMeta) {
				if (meta.getName().equalsIgnoreCase(p.getName())) {
					p.setType(meta.getType());
					p.setPosition(meta.getPosition());
					break;
				}
			}
			parameters.add(p);
		}

		return parameters;
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateByFreeSql(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}

}
