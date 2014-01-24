package com.ctrip.platform.dal.daogen.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.pojo.AutoTask;
import com.ctrip.platform.dal.daogen.pojo.FieldMeta;
import com.ctrip.platform.dal.daogen.pojo.Method;
import com.ctrip.platform.dal.daogen.pojo.Parameter;
import com.ctrip.platform.dal.daogen.pojo.SpTask;
import com.ctrip.platform.dal.daogen.pojo.Task;

public class CSharpGenerator extends AbstractGenerator {

	private CSharpGenerator() {

	}

	private static CSharpGenerator instance = new CSharpGenerator();

	public static CSharpGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateAutoSqlCode(List<Task> tasks) {
		Map<String, List<Task>> groupByTable = groupByTableName(tasks);
	
		VelocityContext context = new VelocityContext();

		context.put("namespace", namespace);

		for (Map.Entry<String, List<Task>> entry : groupByTable.entrySet()) {
			String table = entry.getKey();
			List<Task> objs = entry.getValue();
			if (objs.size() > 0) {
				context.put("database", objs.get(0).getDb_name());
			} else {
				continue;
			}
			context.put("dao_name", String.format("%sDAO", table));
			context.put("CSharpDbTypeMap", Consts.CSharpDbTypeMap);
			context.put("CSharpSqlTypeMap", Consts.CSharpSqlTypeMap);
			context.put("table_name", table);
			context.put("pojo_name", WordUtils.capitalizeFully(table));

			List<FieldMeta> fieldsMeta = getMetaData(context.get("database").toString(), table);
			String primaryKey = "";
			for(FieldMeta meta : fieldsMeta){
				if(meta.isPrimary()){
					primaryKey = meta.getName();
					break;
				}
			}
			context.put("fields", fieldsMeta);

			List<Method> methods = new ArrayList<Method>();
			List<Method> spMethods = new ArrayList<Method>();

			for (Task task : objs) {
				AutoTask obj = (AutoTask)task;
				
				if(null == obj.getMethod_name() || obj.getMethod_name().isEmpty()){
					continue;
				}
				
				Method m = new Method();
				m.setAction(obj.getCrud_type().toLowerCase());
				m.setMethodName(obj.getMethod_name());
				m.setSqlSPName(obj.getSql_content());

				// 查询，或者SQL形式的删除
				if (m.getAction().equalsIgnoreCase("select")
						|| (m.getAction().equalsIgnoreCase("delete") && obj.getSql_type().equalsIgnoreCase("sql"))) {
					m.setParameters(getParametersByCondition(obj.getCondition(), fieldsMeta));
					methods.add(m);
				}
				// SQL形式的更新
				else if (m.getAction().equalsIgnoreCase("update") && obj.getSql_type().equalsIgnoreCase("sql")) {
					List<Parameter> parameters = new ArrayList<Parameter>();
					parameters.addAll(getParametersByCondition(obj.getCondition(), fieldsMeta));
					parameters.addAll(getParametersByFields(obj.getFields(), fieldsMeta));
					m.setParameters(parameters);
					methods.add(m);
				}
				// SP形式的删除
				else if (m.getAction().equalsIgnoreCase("delete") && !obj.getSql_type().equalsIgnoreCase("sql")) {
					List<Parameter> parameters = new ArrayList<Parameter>();
					Parameter p = new Parameter();
					p.setName(primaryKey);
					p.setFieldName(primaryKey);
					for (FieldMeta field : fieldsMeta) {
						if (field.getName().equalsIgnoreCase(primaryKey)) {
							p.setType(field.getType());
							p.setParamMode("IN");
							p.setPosition(1);
							break;
						}
					}
					parameters.add(p);
					m.setParameters(parameters);
					spMethods.add(m);
				}
				// SQL形式的插入
				else if (m.getAction().equalsIgnoreCase("insert") && obj.getSql_type().equalsIgnoreCase("sql")) {
					m.setParameters(getParametersByFields(obj.getFields(), fieldsMeta));
					methods.add(m);
				}
				// SP形式的插入，SP形式的Update
				else {
					List<String> allFields = new ArrayList<String>();
					for(FieldMeta meta : fieldsMeta){
						allFields.add(meta.getName());
					}
					List<Parameter> parameters = getParametersByFields(StringUtils.join(allFields.toArray(), ","), fieldsMeta);
					
					for(Parameter p : parameters){
						if(p.getName().equals(primaryKey)){
							p.setParamMode("OUT");
							break;
						}
					}
					
					m.setParameters(parameters);
					spMethods.add(m);
				}

			}
			context.put("methods", methods);
			context.put("sp_methods", spMethods);
			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			try {
				File projectFile = new File(projectId);
				if (!projectFile.exists()) {
					projectFile.mkdir();
				}
				File csharpFile = new File(projectFile, "csharp");
				if (!csharpFile.exists()) {
					csharpFile.mkdir();
				}
				daoWriter = new FileWriter(String.format("%s/csharp/%s.cs",
						projectId, context.get("dao_name")));
				pojoWriter = new FileWriter(String.format("%s/csharp/%s.cs",
						projectId, context.get("pojo_name")));
				Velocity.mergeTemplate("DAO.cs.tpl", "UTF-8", context,
						daoWriter);
				Velocity.mergeTemplate("POJO.cs.tpl", "UTF-8", context,
						pojoWriter);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					if (null != daoWriter) {
						daoWriter.close();
					}
					if (null != pojoWriter) {
						pojoWriter.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	@Override
	public void generateSPCode(List<Task> tasks) {
		Map<String, List<Task>> groupbyDB = groupByDbName(tasks);

		VelocityContext context = new VelocityContext();

		context.put("namespace", namespace);

		for (Map.Entry<String, List<Task>> entry : groupbyDB.entrySet()) {
			String sp = entry.getKey();
			List<Task> objs = entry.getValue();
			if (objs.size() > 0) {
				context.put("database", objs.get(0).getDb_name());
			} else {
				continue;
			}
			context.put("dao_name",
					String.format("%sSPDAO", context.get("database")));
			context.put("CSharpDbTypeMap", Consts.CSharpDbTypeMap);
			context.put("CSharpSqlTypeMap", Consts.CSharpSqlTypeMap);

			List<Method> spMethods = new ArrayList<Method>();

			for (Task task : objs) {
				
				SpTask obj = (SpTask)task;

				Method m = new Method();
				m.setAction(obj.getCrud_type());
				m.setMethodName(obj.getSp_name());
				m.setSqlSPName(String.format("%s.%s", obj.getSp_schema(), obj.getSp_name()));
				
				ResultSet spParams = masterDao.getSPParams(obj.getDb_name(), obj.getSp_schema(), obj.getSp_name());
				
				List<Parameter> parameters = new ArrayList<Parameter>();
				try {
					while(spParams.next()){
						Parameter p = new Parameter();
						String name = spParams.getString(1);
						if (name.startsWith("@") || name.startsWith(":")) {
							name = name.substring(1);
						}
						p.setName(name);
						p.setFieldName(name);
						p.setType(Consts.CSharpSqlTypeMap.get(spParams.getString(2)));
						p.setParamMode(spParams.getString(3));
						p.setPosition(spParams.getInt(4));
						parameters.add(p);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				m.setParameters(parameters);
				spMethods.add(m);
			}

			context.put("sp_methods", spMethods);
			FileWriter daoWriter = null;

			try {
				File projectFile = new File(projectId);
				if (!projectFile.exists()) {
					projectFile.mkdir();
				}
				File csharpFile = new File(projectFile, "csharp");
				if (!csharpFile.exists()) {
					csharpFile.mkdir();
				}
				daoWriter = new FileWriter(String.format("%s/csharp/%s.cs",
						projectId, context.get("dao_name")));

				Velocity.mergeTemplate("SPDAO.cs.tpl", "UTF-8", context,
						daoWriter);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					if (null != daoWriter) {
						daoWriter.close();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	@Override
	public void generateFreeSqlCode(List<Task> tasks) {
		// TODO Auto-generated method stub

	}

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
		for(String field : fieldsArray){
			Parameter p = new Parameter();
			p.setName(field);
			p.setFieldName(field);
			for(FieldMeta meta : fieldsMeta){
				if(meta.getName().equalsIgnoreCase(field)){
					p.setType(Consts.CSharpSqlTypeMap.get(meta.getType()));
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
			for(FieldMeta meta : fieldsMeta){
				if(meta.getName().equalsIgnoreCase(p.getName())){
					p.setType(Consts.CSharpSqlTypeMap.get(meta.getType()));
					p.setPosition(meta.getPosition());
					break;
				}
			}
			parameters.add(p);
		}
		
		return parameters;
	}

}
