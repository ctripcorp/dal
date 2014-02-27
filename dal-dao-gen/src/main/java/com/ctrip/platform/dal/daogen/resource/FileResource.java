package com.ctrip.platform.dal.daogen.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.pojo.W2uiElement;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.ZipFolder;

@Resource
@Singleton
@Path("file")
public class FileResource {
	
	private static DaoOfProject daoOfProject;
	
	
	static{
		Configuration.addResource("conf.properties");
		daoOfProject = SpringBeanGetter.getDaoOfProject();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<W2uiElement> getFiles(@QueryParam("id") String id,
			@QueryParam("name") String name,
			@QueryParam("parent") boolean parent,
			@QueryParam("root") boolean root) {
		List<W2uiElement> files = new ArrayList<W2uiElement>();

		File currentProjectDir = new File("gen", id);
		if (currentProjectDir.exists()) {

			if (root) {
				for (File f : currentProjectDir.listFiles()) {
					W2uiElement element = new W2uiElement();
					element.setCurrentId(String.format("%s_2_%d", id,
							files.size()));
					element.setRelativeName(f.getName());
					element.setName(f.getName());
					element.setParent(f.isDirectory());
					files.add(element);
				}
			} else if (parent) {
				File currentFile = new File(currentProjectDir, name);
				for (File f : currentFile.listFiles()) {
					W2uiElement element = new W2uiElement();
					element.setCurrentId(String.format("%s_%s_%d", id,
							name.replace("\\", ""), files.size()));
					element.setRelativeName(name + File.separator + f.getName());
					element.setName(f.getName());
					element.setParent(f.isDirectory());
					files.add(element);
				}
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

	@GET
	@Path("download")
	@Produces(MediaType.TEXT_PLAIN)
	public String download(@QueryParam("id") String id,
			@QueryParam("name") String name) {
		File f = null;
		if (null != name && !name.isEmpty()) {
			f = new File(new File("gen", id), name);
		} else {
			f = new File("gen", id);
		}
		
		Project proj = 	daoOfProject.getProjectByID(Integer.valueOf(id));

		String zipFileName = proj.getName() + "-" + System.currentTimeMillis() + ".zip";

		if (f.isFile()) {
			zipFile(f, zipFileName);
		} else {
			new ZipFolder(f.getAbsolutePath()).zipIt(zipFileName);
		}
		
		FTPClient client = new FTPClient();
		FileInputStream fis = null;

		String ftp_server = Configuration.get("ftp_server");
		int ftp_port = Configuration.getInt("ftp_port");
		
		try {
			String ftp_user = Configuration.get("ftp_user");
			String ftp_pass = Configuration.get("ftp_pass");
		    client.connect(ftp_server, ftp_port);
		    if(ftp_user != null && !ftp_user.isEmpty()){
		    	client.login(ftp_user, ftp_pass);
		    }
		    
		    client.setFileType(FTP.BINARY_FILE_TYPE);

		    //
		    // Create an InputStream of the file to be uploaded
		    //
		    fis = new FileInputStream(new File("gen", zipFileName));

		    //
		    // Store file to server
		    //
		    client.storeFile(zipFileName, fis);
		    client.logout();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (fis != null) {
		            fis.close();
		        }
		        client.disconnect();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

		return String.format("ftp://dal@%s:%d/%s", ftp_server, ftp_port, zipFileName);
	}

	private void zipFile(File fileToZip, String zipFileName) {
		byte[] buffer = new byte[1024];

		FileInputStream in = null;
		ZipOutputStream zos = null;
		try {

			FileOutputStream fos = new FileOutputStream(new File("gen", zipFileName));
			zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(fileToZip.getName());
			zos.putNextEntry(ze);
			in = new FileInputStream(fileToZip);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			JavaIOUtils.closeInputStream(in);
			JavaIOUtils.closeOutputStream(zos);
		}
	}
	
	public static void main(String[] args) {
		FTPClient client = new FTPClient();
		FileInputStream fis = null;

		try {
		    client.connect("172.16.155.151", 21);
		    	System.out.println(client.login("dal", ""));
		    	
		    	System.out.println(client.setFileTransferMode(FTP.BINARY_FILE_TYPE));
		    	System.out.println(client.setFileType(FTP.BINARY_FILE_TYPE));

		    //
		    // Create an InputStream of the file to be uploaded
		    //
		    fis = new FileInputStream(new File("gen", "1.zip"));

		    //
		    // Store file to server
		    //
		    
		    System.out.println(client.storeFile("1.zip", fis));
		    System.out.println(client.logout());
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (fis != null) {
		            fis.close();
		        }
		        client.disconnect();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

	}

}
