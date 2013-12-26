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
import com.ctrip.platform.daogen.gen.CSharpGenerator;
import com.ctrip.platform.daogen.gen.JavaGenerator;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/**
 * The schema of {daogen.project} 
 * { 
 * 		"name": "InternationalFightEntine",
 * 		"namespace": "com.ctrip.flight.intl.engine" 
 * }
 * 
 * @author gawu
 * 
 */
@Resource
@Singleton
@Path("project")
public class ProjectResource {

	private DB daoGenDB;

	private DBCollection projectCollection;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProjects() {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		DBCursor cursor = projectCollection.find();

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
	
	@GET
	@Path("project")
	@Produces(MediaType.APPLICATION_JSON)
	public String getProject(@QueryParam("id") String id) {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		//DBCursor cursor = projectCollection.find();
		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));
		
		DBObject project = projectCollection.findOne(query);

		return JSON.serialize(project);

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addProject(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("namespace") String namespace,
			@FormParam("action") String action) {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		BasicDBObject doc = null;
		BasicDBObject newDoc = null;
		BasicDBObject query = null;
		try {
			doc = new BasicDBObject("name", name).append(
					"namespace", namespace);
			if(null != id && !id.isEmpty()){
				query  = new BasicDBObject().append("_id",  new ObjectId(id));
			}
			
			// Add a new project
			if (action.equals("insert")) {
				projectCollection.insert(doc);
			} else if(action.equals("update")) {
				// Update an exist project
				newDoc = new BasicDBObject();
				newDoc.append("$set", doc);

				projectCollection.update(query, newDoc);
			}else if(action.equals("delete")){
				projectCollection.remove(query);
			}
			return Status.OK;
		} catch (MongoException ex) {
			return Status.ERROR;
		}finally{
			query = null;
			newDoc = null;
			doc = null;
		}

	}
	
	@POST
	@Path("generate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status generateProject(@FormParam("project_id") String id,
			@FormParam("language") String language) {
	
		if(language.equals("java"))
			JavaGenerator.getInstance().generateCode(id);
		else if(language.equals("csharp"))
			CSharpGenerator.getInstance().generateCode(id);
		else if(language.equals("python"))
			;
		
		return Status.OK;
	}

}
