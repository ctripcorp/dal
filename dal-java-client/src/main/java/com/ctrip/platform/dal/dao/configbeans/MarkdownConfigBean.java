package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.Markdown;
import com.ctrip.platform.dal.dao.markdown.MarkupManager;

@BeanMeta(alias = "markdown")
public class MarkdownConfigBean extends ConfigBeanBase{
	@BeanMeta(alias = "Markdown")
	private volatile boolean markdown = false;
	
	@BeanMeta(alias = "AutoMarkup")
	private volatile boolean automarkup = true;
	
	@BeanMeta(alias = "AutoMarkupCount")
	private volatile int autoMarkupCount = 100;
	
	@BeanMeta(alias = "MarkDownDB")
	private volatile String dbMarkdown = "";
	
	@BeanMeta(alias = "AllDB")
	private volatile String alldbs = "";
	
	@BeanMeta(alias = "AutoMarkuplv1")
	private volatile int markuplv1 = 10;
	
	@BeanMeta(alias = "AutoMarkuplv2")
	private volatile int markuplv2 = 3;
	
	@BeanMeta(alias = "AutoMarkupDelay")
	private volatile int autoMarkupDelay = 1;
	
	@BeanMeta(omit = true)
	private Map<String, Markdown> marks = new HashMap<String, Markdown>();
	@BeanMeta(omit = true)
	private Lock lock = new ReentrantLock();
	
	public MarkdownConfigBean(){
		this.addChangeEvent("dbMarkdown", new ChangeEvent() {			
	
			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				if(newVal == null || newVal.isEmpty())
					return;
				String[] tokens = newVal.split(",");
				for (String token : tokens) {
					if(!getAlldbs().contains(token))
						throw new Exception(String.format(
								"The specified database[%s] are not included, pls check", token));
				}
			}

			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				updateMarks(newVal);
			}
		});
	}
	
	public boolean isMarkdown() {
		return this.markdown;
	}

	public void setMarkdown(boolean markdown) {
		this.markdown = markdown;
	}

	public String getDbMarkdown() {
		return this.dbMarkdown;
	}

	public void setDbMarkdown(String dbMarkdown) {
		this.dbMarkdown = dbMarkdown;
	}

	public String getAlldbs() {
		if(this.alldbs.isEmpty())
			this.alldbs = StringUtils.join(DalClientFactory.getAllDB(), ",");
		return this.alldbs;
	}
	
	public Markdown getMarkItem(String key){
		return this.marks.get(key);
	}
	
	public Set<String> getMarks(){
		return this.marks.keySet();
	}
	
	public boolean isMarkdown(String dbname){
		return this.isMarkdown() || this.marks.containsKey(dbname);
	}
	
	public synchronized boolean markdown(String dbname){
		if(!this.marks.containsKey(dbname)){
			this.marks.put(dbname, new Markdown(true, dbname));
			this.dbMarkdown = StringUtils.join(this.getMarks(), ",");
		}			
		return this.marks.containsKey(dbname);
	}
	
	public synchronized void markup(String dbname){
		if(this.marks.containsKey(dbname)){
			this.marks.remove(dbname);
			this.dbMarkdown = StringUtils.join(this.getMarks(), ",");
			MarkupManager.reset(dbname);
		}
	}
	
	private void updateMarks(String newVal) throws Exception{
		lock.lock();
		try{
		Map<String, Markdown> temp = new HashMap<String, Markdown>();
		if(newVal == null || newVal.isEmpty()){
			for (String mark : this.getMarks()) {
				this.marks.remove(mark);
			}
			this.marks = temp;
			return;
		}
		String[] tokens = newVal.split(",");
		for (String token : tokens) {
			//If the current mark down database doesn't contain the new value
			//The new value need to be marked up on auto mark down
			if(!this.marks.containsKey(token))
				this.marks.remove(token);
			temp.put(token, new Markdown(false, token));
		}
		marks = temp;
		}catch(Exception e){
			lock.unlock();
			throw new Exception("Update mark downs bean failed.", e);
		}
		finally{
			lock.unlock();
		}
	}

	public int getMarkuplv1() {
		return markuplv1;
	}

	public void setMarkuplv1(int markuplv1) {
		this.markuplv1 = markuplv1;
	}

	public int getMarkuplv2() {
		return markuplv2;
	}

	public void setMarkuplv2(int markuplv2) {
		this.markuplv2 = markuplv2;
	}

	public boolean isAutomarkup() {
		return automarkup;
	}

	public void setAutomarkup(boolean automarkup) {
		this.automarkup = automarkup;
	}

	public int getAutoMarkupCount() {
		return autoMarkupCount;
	}

	public void setAutoMarkupCount(int autoMarkupCount) {
		this.autoMarkupCount = autoMarkupCount;
	}

	public int getAutoMarkupDelay() {
		return autoMarkupDelay;
	}

	public void setAutoMarkupDelay(int autoMarkupDelay) {
		this.autoMarkupDelay = autoMarkupDelay;
	}
}
