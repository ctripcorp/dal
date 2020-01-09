package com.ctrip.platform.dal.dao.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileReader {

    public static String read(String fileName) {
        URL url = FileReader.class.getClassLoader().getResource(fileName);
        if (url == null)
            throw new RuntimeException(String.format("file '%s' not found", fileName));
        try {
            InputStream is = url.openStream();
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(String.format("read file '%s' failed", fileName));
        }
    }

}
