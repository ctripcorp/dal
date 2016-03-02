package com.ctrip.platform.dal.daogen.resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.domain.W2uiElement;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.ctrip.platform.dal.daogen.utils.ZipFolder;
import com.google.common.base.Charsets;

@Resource
@Singleton
@Path("file")
public class FileResource {
	private static String generatePath;

	static {
		generatePath = Configuration.get("gen_code_path");
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<W2uiElement> getFiles(@QueryParam("id") String id, @QueryParam("language") String language,
			@QueryParam("name") String name) {
		List<W2uiElement> files = new ArrayList<W2uiElement>();

		File currentProjectDir = new File(new File(generatePath, id), language);
		if (currentProjectDir.exists()) {
			File currentFile = null;
			if (null == name || name.isEmpty()) {
				currentFile = currentProjectDir;
			} else {
				currentFile = new File(currentProjectDir, name);
			}
			for (File f : currentFile.listFiles()) {
				W2uiElement element = new W2uiElement();
				if (null == name || name.isEmpty()) {
					element.setId(String.format("%s_%d", id, files.size()));
				} else {
					element.setId(String.format("%s_%s_%d", id, name.replace("\\", ""), files.size()));
				}
				if (null == name || name.isEmpty()) {
					element.setData(f.getName());
				} else {
					element.setData(name + File.separator + f.getName());
				}
				element.setText(f.getName());
				element.setChildren(f.isDirectory());
				if (element.isChildren()) {
					element.setType("folder");
					element.setIcon("glyphicon glyphicon-folder-open");
				} else {
					element.setType("file");
					element.setIcon("glyphicon glyphicon-file");
				}
				files.add(element);
			}
		}
		java.util.Collections.sort(files);
		return files;
	}

	@GET
	@Path("content")
	@Produces(MediaType.TEXT_PLAIN)
	public String getFileContent(@QueryParam("id") String id, @QueryParam("language") String language,
			@QueryParam("name") String name) {
		File f = new File(new File(new File(generatePath, id), language), name);
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return sb.toString();
	}

	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public String download(@QueryParam("id") String id, @QueryParam("language") String name,
			@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

		File f = null;
		if (null != name && !name.isEmpty()) {
			f = new File(new File(generatePath, id), name);
		} else {
			f = new File(generatePath, id);
		}

		Project proj = SpringBeanGetter.getDaoOfProject().getProjectByID(Integer.valueOf(id));
		DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format1.format(new Date());
		final String zipFileName = proj.getName() + "-" + date + ".zip";

		if (f.isFile()) {
			zipFile(f, zipFileName);
		} else {
			new ZipFolder(f.getAbsolutePath()).zipIt(zipFileName);
		}

		FileInputStream fis = null;
		BufferedInputStream buff = null;
		OutputStream myout = null;

		String path = generatePath + "/" + zipFileName;

		File file = new File(path);

		try {
			if (!file.exists()) {
				response.sendError(404, "File not found!");
				return "";
			} else {
				response.setContentType("application/zip;charset=utf-8");
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition",
						"attachment;filename=" + new String(file.getName().getBytes(Charsets.UTF_8), "UTF-8"));
			}
			// response.reset();
			fis = new FileInputStream(file);
			buff = new BufferedInputStream(fis);
			byte[] b = new byte[1024];
			long k = 0;
			myout = response.getOutputStream();

			while (k < file.length()) {
				int j = buff.read(b, 0, 1024);
				k += j;
				myout.write(b, 0, j);
			}
			myout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (buff != null)
					buff.close();
				if (myout != null)
					myout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "";

	}

	private void zipFile(File fileToZip, String zipFileName) {
		byte[] buffer = new byte[1024];

		FileInputStream in = null;
		ZipOutputStream zos = null;
		try {

			FileOutputStream fos = new FileOutputStream(new File(generatePath, zipFileName));
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

}
