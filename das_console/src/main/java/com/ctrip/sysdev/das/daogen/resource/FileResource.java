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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.ctrip.sysdev.das.daogen.MongoClientManager;
import com.ctrip.sysdev.das.daogen.domain.ZTreeElement;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Resource
@Singleton
@Path("file")
public class FileResource {

	private DB daoGenDB;

	private DBCollection projectCollection;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ZTreeElement> getProjects(@QueryParam("id") String id,
			@QueryParam("name") String name, @QueryParam("type") String type,
			@QueryParam("parent") String parent) {

		if (null == daoGenDB) {
			MongoClient client = MongoClientManager.getDefaultMongoClient();
			daoGenDB = client.getDB("daogen");
		}

		if (null == projectCollection) {
			projectCollection = daoGenDB.getCollection("project");
		}

		List<ZTreeElement> allElements = new ArrayList<ZTreeElement>();

		if (null != type && type.equals("all")) {

			DBCursor projects = projectCollection.find();
			while (projects.hasNext()) {
				DBObject current = projects.next();
				ZTreeElement p = new ZTreeElement();
				p.setId(current.get("_id").toString());
				p.setName(current.get("name").toString());
				p.setType("project");
				p.setIsParent(null == parent ? true : Boolean.valueOf(parent));
				allElements.add(p);
			}
			return allElements;
		} else if (null != type && type.equals("project")) {
			File currentProjectDir = new File(id);
			if (currentProjectDir.exists()) {
//				for (File f : FileUtils.listFilesAndDirs(currentProjectDir,
//						new NotFileFilter(TrueFileFilter.INSTANCE),
//						DirectoryFileFilter.DIRECTORY)) {
//					if (!f.getName().equals(id)) {
//						Project p = new Project();
//						p.setId(id);
//						p.setName(f.getName());
//						p.setType("folder");
//						p.setIsParent(true);
//						allProjects.add(p);
//					}
//				}
				for (File f : FileUtils.listFiles(currentProjectDir,
						new String[] { "cs", "java" }, true)) {
					ZTreeElement p = new ZTreeElement();
					p.setId(id);
					p.setName(String.format("%s/%s", f.getParentFile().getName(), f.getName()));
					p.setType("file");
					p.setIsParent(false);
					allElements.add(p);
				}
			}
		}
//		else if (null != type && type.equals("folder")) {
//			File currentProjectDir = new File(id, name);
//			for (File f : FileUtils.listFiles(currentProjectDir, new String[] {
//					"cs", "java" }, false)) {
//				Project p = new Project();
//				p.setId(id);
//				p.setName(String.format("%s/%s", name, f.getName()));
//				p.setType("file");
//				p.setIsParent(false);
//				allProjects.add(p);
//			}
//		}

		return allElements;
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
				while ((line = reader.readLine()) != null) {
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
