package com.ctrip.platform.dal.daogen.resource;

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

import com.ctrip.platform.dal.daogen.pojo.Project;

@Resource
@Singleton
@Path("file")
public class FileResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Project> getFiles(@QueryParam("id") String id) {
		List<Project> files = new ArrayList<Project>();

		File currentProjectDir = new File("gen", id);
		if (currentProjectDir.exists()) {
			for (File f : FileUtils.listFiles(currentProjectDir, new String[] {
					"cs", "java" }, true)) {
				Project p = new Project();
				p.setId(Integer.valueOf(id));
				p.setName(String.format("%s/%s", f.getParentFile().getName(),
						f.getName()));
				files.add(p);
			}
		}

		return files;
	}

	@GET
	@Path("content")
	@Produces(MediaType.TEXT_PLAIN)
	public String getFileContent(@QueryParam("id") String id,
			@QueryParam("name") String name) {
		File f = new File(new File("gen", id), name);
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
