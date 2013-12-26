package com.ctrip.platform.daogen.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.bson.types.ObjectId;

import com.ctrip.platform.daogen.MongoClientManager;
import com.ctrip.platform.daogen.domain.MasterDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/**
 * The schema of {daogen.task} { "project_id": , "task_type": , "database" : ,
 * "table": , "dao_name": , "func_name": , "sql_spname": , "fields": ,
 * "condition": , "crud": } The schema of {daogen.task_meta} { "database" : ,
 * "table": , "primary_key": , "fields": }
 * 
 * @author gawu
 * 
 */
@Resource
@Singleton
@Path("task")
public class TaskResource {

	private DB daoGenDB;

	private DBCollection taskCollection;

	// private DBCollection taskMetaCollection;

	private static MasterDAO master;

	static {
		master = new MasterDAO();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getTasks(@QueryParam("project_id") String id) {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == taskCollection) {
			taskCollection = daoGenDB.getCollection("task");
		}

		BasicDBObject query = new BasicDBObject().append("project_id", id);
		DBCursor cursor = taskCollection.find(query);

		List<DBObject> results = new ArrayList<DBObject>();

		try {
			while (cursor.hasNext()) {
				results.add(cursor.next());
			}
		} finally {
			cursor.close();
		}

		return JSON.serialize(results);

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addProject(@FormParam("id") String id,
			@FormParam("project_id") String projectId,
			@FormParam("task_type") String taskType,
			@FormParam("database") String database,
			@FormParam("table") String table,
			@FormParam("dao_name") String daoName,
			@FormParam("func_name") String funcName,
			@FormParam("sql_spname") String sqlSpName,
			@FormParam("fields") String fields,
			@FormParam("condition") String condition,
			@FormParam("crud") String crud, @FormParam("cud") String cud,
			@FormParam("action") String action) {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == taskCollection) {
			taskCollection = daoGenDB.getCollection("task");
		}

		BasicDBObject doc = null;
		BasicDBObject newDoc = null;
		BasicDBObject query = null;
		try {

			if (null != id && !id.isEmpty()) {
				query = new BasicDBObject().append("_id", new ObjectId(id));
			}

			if (action.equals("delete")) {
				taskCollection.remove(query);
				return Status.OK;
			}

			doc = new BasicDBObject("project_id", projectId)
					.append("task_type", taskType).append("database", database)
					.append("table", table).append("dao_name", daoName)
					.append("func_name", funcName)
					.append("sql_spname", sqlSpName)
					.append("fields", JSON.parse(fields))
					.append("condition", JSON.parse(condition))
					.append("cud", cud).append("crud", crud);

			// Add a new project
			if (action.equals("insert")) {

				taskCollection.insert(doc);

				// if (null == taskMetaCollection) {
				// taskMetaCollection = daoGenDB.getCollection("task_meta");
				// }
				//
				// BasicDBObject metaQuery = new BasicDBObject().append(
				// "database", database).append("table", table);
				//
				// if (taskMetaCollection.find(metaQuery) == null) {
				// ResultSet rs = master.getPrimaryKey(daoName, table);
				// String primaryKey = "";
				// if (rs.next()) {
				// primaryKey = rs.getString(1);
				// }
				// metaQuery.append("primary_key", primaryKey);
				// }

			} else if (action.equals("update")) {
				// Update an exist project
				newDoc = new BasicDBObject();
				newDoc.append("$set", doc);

				taskCollection.update(query, newDoc);
			}
			return Status.OK;
		} catch (MongoException ex) {
			ex.printStackTrace();
		}
		// catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		finally {
			query = null;
			newDoc = null;
			doc = null;
		}

		return Status.ERROR;

	}

}
