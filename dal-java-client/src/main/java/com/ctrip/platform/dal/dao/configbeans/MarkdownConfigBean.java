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

@BeanMeta(alias = "arch-data-common-bean-markdownmarkupbean")
public class MarkdownConfigBean extends ConfigBeanBase{
	@BeanMeta(alias = "AppMarkDown")
	private volatile boolean markdown = false;
	
	@BeanMeta(alias = "EnableAutoMarkDown")
	private volatile boolean ennableAutoMarkDown = true;
	
	@BeanMeta(alias = "AutoMarkUpVolume")
	private volatile int autoMarkupBatches = -1;
	
	@BeanMeta(alias = "MarkDownKeys")
	private volatile String dbMarkdown = "";
	
	@BeanMeta(alias = "AllInOneKeys")
	private volatile String alldbs = "";
	
	@BeanMeta(alias = "AutoMarkUpSchedule")
	private volatile String markUpSchedule = "1,3,5";
	
	@BeanMeta(alias = "AutoMarkUpDelay")
	private volatile int autoMarkupDelay = 60;
	
	@BeanMeta(omit = true)
	private int[] autoMarkUpSchedule = new int[]{1, 3, 5};
	
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
		
		this.addChangeEvent("markUpSchedule", new ChangeEvent() {			
			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				if(newVal == null || newVal.isEmpty())
					throw new Exception();
				String[] tokens = newVal.trim().split(",");
				int[] temp = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					temp[i] = Integer.parseInt(tokens[i]);
					if(temp[i] < 1 && temp[i] > 9){
						throw new Exception("The auto mark up schedule must be greater than 0 and lesser than 9");
					}
					if(i > 0 && temp[i] <= temp[i-1]){
						throw new Exception("The auto mark up schedule must be ascending order");
					}
				}
				autoMarkUpSchedule = temp;
			}
			
			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				// TODO Auto-generated method stub		
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
			this.marks.put(dbname, new Markdown(true));
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
			temp.put(token, new Markdown(false));
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

	public boolean isEnnableAutoMarkDown() {
		return ennableAutoMarkDown;
	}

	public void setEnnableAutoMarkDown(boolean ennableAutoMarkDown) {
		this.ennableAutoMarkDown = ennableAutoMarkDown;
	}

	public int getAutoMarkupBatches() {
		return autoMarkupBatches;
	}

	public void setAutoMarkupBatches(int autoMarkupBatches) {
		this.autoMarkupBatches = autoMarkupBatches;
	}

	public int getAutoMarkupDelay() {
		return autoMarkupDelay;
	}

	public void setAutoMarkupDelay(int autoMarkupDelay) {
		this.autoMarkupDelay = autoMarkupDelay;
	}

	public String getMarkUpSchedule() {
		return markUpSchedule;
	}

	public void setMarkUpSchedule(String markUpSchedule) {
		this.markUpSchedule = markUpSchedule;
	}

	public int[] getAutoMarkUpSchedule() {
		return autoMarkUpSchedule;
	}
}
