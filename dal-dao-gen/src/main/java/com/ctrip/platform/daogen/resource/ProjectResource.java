package com.ctrip.platform.daogen.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.ctrip.platform.daogen.dao.ProjectDAO;
import com.ctrip.platform.daogen.gen.CSharpGenerator;
import com.ctrip.platform.daogen.gen.JavaGenerator;
import com.ctrip.platform.daogen.pojo.Project;
import com.ctrip.platform.daogen.pojo.Status;

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

//	private DB daoGenDB;
//
//	private DBCollection projectCollection;
	
	private static ProjectDAO projectDao = new ProjectDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects() {
		
		ResultSet results = projectDao.getAllProjects();
		
		List<Project> projects = new ArrayList<Project>();
		
		try {
			while(results.next()){
				Project proj = new Project();
				proj.setId(results.getInt(1));
				proj.setUser_id(results.getInt(2));
				proj.setName(results.getString(3));
				proj.setNamespace(results.getString(4));
				projects.add(proj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return projects;

//		if (null == daoGenDB) {
//			MongoClient client = MongoClientManager.getDefaultMongoClient();
//			daoGenDB = client.getDB("daogen");
//		}
//
//		if (null == projectCollection) {
//			projectCollection = daoGenDB.getCollection("project");
//		}

//		DBCursor cursor = projectCollection.find();
//
//		List<DBObject> results = new ArrayList<DBObject>();
//
//		try {
//			while (cursor.hasNext()) {
//				results.add(cursor.next());
//			}
//		} finally {
//			cursor.close();
//		}

		//return JSON.serialize(results);

	}
	
	@GET
	@Path("project")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProject(@QueryParam("id") String id) {
		
		ResultSet results = projectDao.getProjectByID(Integer.valueOf(id));
		
		Project proj = new Project();
		
		try {
			if(results.next()){
				proj.setId(results.getInt(1));
				proj.setUser_id(results.getInt(2));
				proj.setName(results.getString(3));
				proj.setNamespace(results.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return proj;

//		if (null == daoGenDB) {
//			MongoClient client = MongoClientManager.getDefaultMongoClient();
//			daoGenDB = client.getDB("daogen");
//		}
//
//		if (null == projectCollection) {
//			projectCollection = daoGenDB.getCollection("project");
//		}
//
//		//DBCursor cursor = projectCollection.find();
//		BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(id));
//		
//		DBObject project = projectCollection.findOne(query);
//
//		return JSON.serialize(project);

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Status addProject(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("namespace") String namespace,
			@FormParam("action") String action) {
		
		if (action.equals("insert")) {
			Project proj = new Project();
			proj.setUser_id(0);
			proj.setName(name);
			proj.setNamespace(namespace);
			projectDao.insertProject(proj);
		} else if (action.equals("update")) {
			Project proj = new Project();
			proj.setId(Integer.valueOf(id));
			proj.setUser_id(0);
			proj.setName(name);
			proj.setNamespace(namespace);
			projectDao.updateProject(proj);
		} else if (action.equals("delete")) {
			Project proj = new Project();
			proj.setId(Integer.valueOf(id));
//			proj.setUser_id(0);
//			proj.setName(name);
//			proj.setNamespace(namespace);
			projectDao.deleteProject(proj);
		}
		return Status.OK;

//		if (null == daoGenDB) {
//			MongoClient client = MongoClientManager.getDefaultMongoClient();
//			daoGenDB = client.getDB("daogen");
//		}
//
//		if (null == projectCollection) {
//			projectCollection = daoGenDB.getCollection("project");
//		}
//
//		BasicDBObject doc = null;
//		BasicDBObject newDoc = null;
//		BasicDBObject query = null;
//		try {
//			doc = new BasicDBObject("name", name).append(
//					"namespace", namespace);
//			if(null != id && !id.isEmpty()){
//				query  = new BasicDBObject().append("_id",  new ObjectId(id));
//			}
//			
//			// Add a new project
//			if (action.equals("insert")) {
//				projectCollection.insert(doc);
//			} else if(action.equals("update")) {
//				// Update an exist project
//				newDoc = new BasicDBObject();
//				newDoc.append("$set", doc);
//
//				projectCollection.update(query, newDoc);
//			}else if(action.equals("delete")){
//				projectCollection.remove(query);
//			}
//			return Status.OK;
//		} catch (MongoException ex) {
//			return Status.ERROR;
//		}finally{
//			query = null;
//			newDoc = null;
//			doc = null;
//		}

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
