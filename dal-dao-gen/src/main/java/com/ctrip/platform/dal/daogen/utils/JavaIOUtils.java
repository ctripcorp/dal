package com.ctrip.platform.dal.daogen.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public abstract class JavaIOUtils {

	/**
	 * Close the given Java IO Writer and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual io code.
	 * 
	 * @param writer
	 */
	public static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Close the given Java IO Reader and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual io code.
	 * 
	 * @param reader
	 */
	public static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void closeInputStream(InputStream stream){
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void closeOutputStream(OutputStream stream){
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

}
