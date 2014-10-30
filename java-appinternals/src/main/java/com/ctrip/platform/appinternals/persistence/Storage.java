package com.ctrip.platform.appinternals.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.appinternals.helpers.Helper;

public class Storage {
	private static Logger logger = LoggerFactory.getLogger(Storage.class);
	private static final String FILE_NAME = "beans.properties";
	private Properties prop;
	private String dir = "";
	private static Date now = new Date();
	
	public Storage(String path){
		this.dir = path;
		this.prop = new Properties();
	}
	
	public void set(String key, String val){
		this.prop.setProperty(key, val);
	}
	
	public String get(String key){
		return this.prop.getProperty(key);
	}
	
	public void load(){
		File file = this.getPropertiesFile();
		if(file.exists()){
			InputStream ins = null;
			try {
				ins = new FileInputStream(file);
				this.prop.load(ins);
			} catch (Exception e) {
				logger.error(String.format("Get properties[%s] failed.", file.getPath()), e);
			}
			finally{
				Helper.close(ins);
			}
		}
	}
	
	public void save(){
		File file = this.getPropertiesFile();
		OutputStream ous = null;
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			ous = new FileOutputStream(file);
			now = new Date();
			this.prop.store(ous, "Update at: " + now.toString());
		}catch(Exception e){
			logger.error(String.format("Save properties[%] failed.", file.getPath()), e);
		}
	}
	
	public String getPath(){
		return getPropertiesFile().getPath();
	}
	
	private File getPropertiesFile(){
		if(this.dir.endsWith("/") || this.dir.endsWith("\\")){
			new File(this.dir + FILE_NAME);
		}
		return new File(this.dir + "/" + FILE_NAME);
	}
}
