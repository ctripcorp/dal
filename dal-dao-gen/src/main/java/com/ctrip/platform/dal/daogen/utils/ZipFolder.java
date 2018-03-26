package com.ctrip.platform.dal.daogen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    private static String generatePath;

    static {
        generatePath = Configuration.get("gen_code_path");
    }

    List<String> fileList;
    String zipFolder;

    public ZipFolder(String zipFolder) {
        this.zipFolder = zipFolder;
        fileList = new ArrayList<>();
    }

    public void zipIt(String zipFile) {
        generateFileList(new File(zipFolder));
        byte[] buffer = new byte[1024];

        try {
            FileOutputStream fos = new FileOutputStream(new File(generatePath, zipFile));
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (String file : this.fileList) {
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(this.zipFolder + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     */
    public void generateFileList(File node) {
        //add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(this.zipFolder.length() + 1, file.length());
    }

}
