package com.ctrip.sysdev.das.daogen.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.bson.types.ObjectId;

import com.ctrip.sysdev.das.daogen.DaoGenResources;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class JavaGenerator {

	private DB daoGenDB;

	private DBCollection projectCollection;

	private DBCollection taskCollection;

	private DBCollection taskMetaCollection;

	private String namespace;
	
	private String projectId;

	static {
		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	public boolean generateCode(String projectId) {

		if (null == daoGenDB) {
			MongoClient client = DaoGenResources.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == taskCollection) {
			taskCollection = daoGenDB.getCollection("task");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		BasicDBObject projectQuery = new BasicDBObject().append("_id",
				new ObjectId(projectId));

		DBObject project = projectCollection.findOne(projectQuery);
		if (null != project) {
			namespace = project.get("namespace").toString();
			this.projectId = projectId;
		}

		BasicDBObject query = new BasicDBObject().append("project_id",
				projectId);

		// 自动生成的SQL
		query.append("task_type", "autosql");
		DBCursor cursor = taskCollection.find(query);

		List<DBObject> autoSql = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			autoSql.add(obj);
		}
		cursor.close();
		generateAutoSqlCode(autoSql);

		// 存储过程
		query.removeField("task_type");
		query.append("task_type", "sp");

		cursor = taskCollection.find(query);

		List<DBObject> sp = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			sp.add(obj);
		}
		cursor.close();
		generateSPCode(sp);

		// 手工编写的SQL
		query.removeField("task_type");
		query.append("task_type", "freesql");

		cursor = taskCollection.find(query);

		List<DBObject> freeSql = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			freeSql.add(obj);
		}
		cursor.close();
		generateFreeSqlCode(freeSql);

		return true;
	}

	/**
	 * 为自动生成的SQL生成相应的代码，A SQL a function, a table a DAO
	 * 
	 * @param tasks
	 */
	private void generateAutoSqlCode(List<DBObject> tasks) {

		Map<String, List<DBObject>> groupByTable = new HashMap<String, List<DBObject>>();

		for (DBObject t : tasks) {
			Object table = t.get("table");
			if (groupByTable.containsKey(table)) {
				groupByTable.get(table).add(t);
			} else {
				List<DBObject> objs = new ArrayList<DBObject>();
				objs.add(t);
				groupByTable.put(table.toString(), objs);
			}
		}

		VelocityContext context = new VelocityContext();

		context.put("namespace", namespace);

		for (Map.Entry<String, List<DBObject>> entry : groupByTable.entrySet()) {
			String table = entry.getKey();
			List<DBObject> objs = entry.getValue();
			if (objs.size() > 0) {
				context.put("database", objs.get(0).get("database"));
			} else {
				continue;
			}
			context.put("dao_name", String.format("%sDAO", table));
			context.put("JavaDbTypeMap", Consts.JavaDbTypeMap);

			if (null == taskMetaCollection) {
				taskMetaCollection = daoGenDB.getCollection("task_meta");
			}

			BasicDBObject metaQuery = new BasicDBObject().append("database",
					context.get("database")).append("table", table);

			String primaryKey = "";
			DBObject fieldTypeMap = null;

			DBObject metaData = taskMetaCollection.findOne(metaQuery);

			if (null != metaData) {
				primaryKey = metaData.get("primary_key").toString();
				fieldTypeMap = (DBObject) metaData.get("fields");
			}

			List<Method> methods = new ArrayList<Method>();
			List<Method> spMethods = new ArrayList<Method>();

			for (DBObject obj : objs) {
				Object crud = obj.get("crud");
				Object cud = obj.get("cud");

				Method m = new Method();
				m.setAction(crud.toString());
				m.setMethodName(obj.get("func_name").toString());
				m.setSqlSPName(obj.get("sql_spname").toString());

				// 查询，或者SQL形式的删除
				if (crud.equals("select")
						|| (crud.equals("delete") && cud.equals("sql"))) {
					m.setParameters(getParametersByCondition(obj, fieldTypeMap));
					methods.add(m);
				}
				// SQL形式的更新
				else if (crud.equals("update") && cud.equals("sql")) {
					List<Parameter> parameters = new ArrayList<Parameter>();
					parameters.addAll(getParametersByCondition(obj,
							fieldTypeMap));
					parameters.addAll(getParametersByFields(obj, fieldTypeMap));
					m.setParameters(parameters);
					methods.add(m);
				}
				// SP形式的删除
				else if (crud.equals("delete") && !cud.equals("sql")) {
					List<Parameter> parameters = new ArrayList<Parameter>();
					Parameter p = new Parameter();
					p.setName(primaryKey);
					p.setType(fieldTypeMap.get(primaryKey).toString());
					parameters.add(p);
					m.setParameters(parameters);
					spMethods.add(m);
				}
				// SQL形式的插入
				else if (crud.equals("insert") && cud.equals("sql")) {
					m.setParameters(getParametersByFields(obj, fieldTypeMap));
					methods.add(m);
				}
				// SP形式的插入，SP形式的Update
				else {
					m.setParameters(getParametersByFields(obj, fieldTypeMap));
					spMethods.add(m);
				}

			}
			context.put("methods", methods);
			context.put("sp_methods", spMethods);
			FileWriter w = null;
			try {
				File projectFile = new File(projectId);
				if(!projectFile.exists()){
					projectFile.mkdir();
				}
				w = new FileWriter(String.format("%s/%s.java",projectId,
						context.get("dao_name")));
				Velocity.mergeTemplate("DAO.java.tpl", "UTF-8", context, w);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != w) {
					try {
						w.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void generateSPCode(List<DBObject> tasks) {
		Map<String, List<DBObject>> groupbyDB = new HashMap<String, List<DBObject>>();

		for (DBObject t : tasks) {
			Object database = t.get("database");
			if (groupbyDB.containsKey(database)) {
				groupbyDB.get(database).add(t);
			} else {
				List<DBObject> objs = new ArrayList<DBObject>();
				objs.add(t);
				groupbyDB.put(database.toString(), objs);
			}
		}

		VelocityContext context = new VelocityContext();

		context.put("namespace", namespace);

		for (Map.Entry<String, List<DBObject>> entry : groupbyDB.entrySet()) {
			String sp = entry.getKey();
			List<DBObject> objs = entry.getValue();
			if (objs.size() > 0) {
				context.put("database", objs.get(0).get("database"));
			} else {
				continue;
			}
			context.put("dao_name",
					String.format("%sSPDAO", context.get("database")));
			context.put("JavaDbTypeMap", Consts.JavaDbTypeMap);

			if (null == taskMetaCollection) {
				taskMetaCollection = daoGenDB.getCollection("task_meta");
			}
			
			List<Method> spMethods = new ArrayList<Method>();

			for (DBObject obj : objs) {
				
				BasicDBObject metaQuery = new BasicDBObject().append("database",
						context.get("database")).append("sp", obj.get("sql_spname"));

				BasicDBList fields = null;

				DBObject metaData = taskMetaCollection.findOne(metaQuery);

				if (null != metaData) {
					fields = (BasicDBList) metaData.get("fields");
				}
				
				Object crud = obj.get("crud");
				Method m = new Method();
				m.setAction(crud.toString());
				String spName = obj.get("sql_spname").toString();
				if (spName.contains(".")) {
					m.setMethodName(spName.substring(spName.indexOf(".") + 1));
				}else{
					m.setMethodName(spName);
				}
				m.setSqlSPName(spName);
				
				BasicDBList params = (BasicDBList)metaData.get("params");
				List<Parameter> parameters = new ArrayList<Parameter>();
				for (Object param : params) {
					Parameter p = new Parameter();
					DBObject bj = (DBObject)param;
					String name = bj.get("name").toString();
					if(name.startsWith("@") || name.startsWith(":")){
						name = name.substring(1);
					}
					p.setName(name);
					p.setType(Consts.JavaSqlTypeMap.get(bj.get("type")));
					parameters.add(p);
				}
				m.setParameters(parameters);
				spMethods.add(m);
			}
			
			context.put("sp_methods", spMethods);
			FileWriter w = null;
			try {
				File projectFile = new File(projectId);
				if(!projectFile.exists()){
					projectFile.mkdir();
				}
				w = new FileWriter(String.format("%s/%s.java",projectId,
						context.get("dao_name")));
				Velocity.mergeTemplate("SPDAO.java.tpl", "UTF-8", context, w);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != w) {
					try {
						w.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}

	}

	private void generateFreeSqlCode(List<DBObject> tasks) {

	}

	/**
	 * 取出task表中的fields字段（List类型），组装为Parameter数组
	 * 
	 * @param obj
	 * @param fieldTypeMap
	 * @return
	 */
	private List<Parameter> getParametersByFields(DBObject obj,
			DBObject fieldTypeMap) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		Object fields = obj.get("fields");
		if (null != fields && fields instanceof BasicDBList) {
			BasicDBList fieldsObj = (BasicDBList) fields;

			for (Object field : fieldsObj) {
				Parameter p = new Parameter();
				p.setName(field.toString());
				p.setType(Consts.JavaSqlTypeMap.get(fieldTypeMap.get(field
						.toString())));
				parameters.add(p);
			}
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
	private List<Parameter> getParametersByCondition(DBObject obj,
			DBObject fieldTypeMap) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		Object condition = obj.get("condition");
		if (null != condition && condition instanceof DBObject) {
			DBObject conditionObj = (DBObject) condition;

			for (String key : conditionObj.keySet()) {
				Parameter p = new Parameter();
				p.setName(key);
				p.setType(Consts.JavaSqlTypeMap.get(fieldTypeMap.get(key)));
				parameters.add(p);
			}
		}
		return parameters;
	}

	public static void main(String[] args) {
		for (File f : File.listRoots()) {
			System.out.println(f.getAbsolutePath());
		}
	}

}
