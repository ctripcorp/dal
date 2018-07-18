package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.domain.W2uiElement;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.entity.UserGroup;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.ZipFolder;
import com.google.common.base.Charsets;

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
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        try {
            List<W2uiElement> files = new ArrayList<>();

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
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @GET
    @Path("content")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFileContent(@QueryParam("id") String id, @QueryParam("language") String language,
            @QueryParam("name") String name) throws Exception {
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
            } catch (Throwable e) {
                LoggerManager.getInstance().error(e);
                throw e;
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

        Project project = BeanGetter.getDaoOfProject().getProjectByID(Integer.valueOf(id));
        boolean isValid = validatePermission(request, project.getDal_group_id());
        if (!isValid) {
            response.sendError(403, "Forbidden!");
            return "";
        }

        DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format1.format(new Date());
        final String zipFileName = project.getName() + "-" + date + ".zip";

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
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
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

    private void zipFile(File fileToZip, String zipFileName) throws Exception {
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

    private boolean validatePermission(HttpServletRequest request, Integer projectGroupId) throws Exception {
        boolean result = false;
        LoginUser user = RequestUtil.getUserInfo(request);
        if (user == null)
            return result;
        try {
            List<UserGroup> userGroups = BeanGetter.getDalUserGroupDao().getUserGroupByUserId(user.getId());
            if (userGroups == null || userGroups.size() == 0)
                return result;
            for (UserGroup group : userGroups) {
                if (group.getGroup_id().intValue() == projectGroupId.intValue()) {
                    result |= true;
                    break;
                }
            }
        } catch (Throwable e) {
        }
        return result;
    }

}
