package com.ctrip.sysdev.das.daogen.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

import com.ctrip.sysdev.das.daogen.DaoGenResources;
import com.ctrip.sysdev.das.daogen.domain.Project;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Resource
@Singleton
@Path("rest/file")
public class FileResource {

	private DB daoGenDB;

	private DBCollection projectCollection;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getProjects(@QueryParam("id") String id,
			@QueryParam("name") String name, @QueryParam("type") String type) {

		if (null == daoGenDB) {
			MongoClient client = DaoGenResources.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		List<Project> allProjects = new ArrayList<Project>();

		if (null != type && type.equals("all")) {

			DBCursor projects = projectCollection.find();
			while (projects.hasNext()) {
				DBObject current = projects.next();
				Project p = new Project();
				p.setId(current.get("_id").toString());
				p.setName(current.get("name").toString());
				p.setType("project");
				p.setIsParent(true);
				allProjects.add(p);
			}
			return allProjects;
		} else if (null != type && type.equals("project")) {
			File currentProjectDir = new File(id);
			if (currentProjectDir.exists()) {
				for (File f : FileUtils.listFiles(currentProjectDir,
						new String[] { "cs", "java" }, false)) {
					Project p = new Project();
					p.setId(id);
					p.setName(f.getName());
					p.setType("file");
					p.setIsParent(false);
					allProjects.add(p);
				}
			}
		}

		return allProjects;
	}

	@GET
	@Path("content")
	@Produces(MediaType.TEXT_PLAIN)
	public String getFileContent(@QueryParam("id") String id,
			@QueryParam("name") String name) {
		File f = new File(id, name);
		StringBuilder sb = new StringBuilder();
		if (f.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(f));
				
				String line = null;
				while((line = reader.readLine()) != null){
					sb.append(line);
					sb.append(System.getProperty("line.separator"));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return sb.toString();
	}

}
