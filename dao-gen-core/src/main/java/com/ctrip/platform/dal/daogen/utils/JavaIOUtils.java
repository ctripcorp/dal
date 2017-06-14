package com.ctrip.platform.dal.daogen.utils;

import java.io.*;

public abstract class JavaIOUtils {

    /**
     * Close the given Java IO Writer and ignore any thrown exception. This is useful for typical finally blocks in
     * manual io code.
     *
     * @param writer
     */
    public static void closeWriter(Writer writer) throws Exception {
        if (writer != null) {
            try {
                writer.close();
            } catch (Throwable e) {
                throw e;
            }
        }
    }

    /**
     * Close the given Java IO Reader and ignore any thrown exception. This is useful for typical finally blocks in
     * manual io code.
     *
     * @param reader
     */
    public static void closeReader(Reader reader) throws Exception {
        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                throw e;
            }
        }
    }

    public static void closeInputStream(InputStream stream) throws Exception {
        if (stream != null) {
            try {
                stream.close();
            } catch (Throwable e) {
                throw e;
            }
        }
    }

    public static void closeOutputStream(OutputStream stream) throws Exception {
        if (stream != null) {
            try {
                stream.close();
            } catch (Throwable e) {
                throw e;
            }
        }
    }

}
