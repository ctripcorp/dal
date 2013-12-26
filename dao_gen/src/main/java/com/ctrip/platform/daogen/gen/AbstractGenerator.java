package com.ctrip.platform.daogen.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.Velocity;
import org.bson.types.ObjectId;

import com.ctrip.platform.daogen.MongoClientManager;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public abstract class AbstractGenerator implements Generator {
	
	protected static DB daoGenDB;

	protected static DBCollection projectCollection;

	protected  static DBCollection taskCollection;

	protected  static DBCollection taskMetaCollection;
	
	protected String namespace;
	
	protected String projectId;
	
	static{
		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}
		
		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		if (null == taskCollection) {
			taskCollection = daoGenDB.getCollection("task");
		}
		
		if (null == taskMetaCollection) {
			taskMetaCollection = daoGenDB.getCollection("task_meta");
		}
		
		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(String projectId) {

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
	 * For a list of DBObject, return a map group by a condition
	 * @param tasks
	 * @param condition
	 * @return
	 */
	protected Map<String, List<DBObject>> groupByCondition(List<DBObject> tasks, String condition){
		Map<String, List<DBObject>> groupBy = new HashMap<String, List<DBObject>>();

		for (DBObject t : tasks) {
			Object conditionValue = t.get(condition);
			if (groupBy.containsKey(conditionValue)) {
				groupBy.get(conditionValue).add(t);
			} else {
				List<DBObject> objs = new ArrayList<DBObject>();
				objs.add(t);
				groupBy.put(conditionValue.toString(), objs);
			}
		}

		return groupBy;
	}

	@Override
	public abstract void generateAutoSqlCode(List<DBObject> tasks);

	@Override
	public  abstract void generateSPCode(List<DBObject> tasks);

	@Override
	public abstract void generateFreeSqlCode(List<DBObject> tasks);


}
