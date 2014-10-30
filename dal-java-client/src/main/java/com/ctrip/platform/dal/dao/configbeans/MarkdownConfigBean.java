package com.ctrip.platform.dal.dao.configbeans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.configuration.ChangeEvent;
import com.ctrip.platform.appinternals.configuration.ConfigBeanBase;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.Markdown;
import com.ctrip.platform.dal.dao.markdown.MarkupManager;

@BeanMeta(alias = "arch-data-common-bean-markdownbean")
public class MarkdownConfigBean extends ConfigBeanBase {
	
	private static Logger logger = LoggerFactory.getLogger(MarkdownConfigBean.class);
	
	@BeanMeta(alias = "AppMarkDown")
	private volatile boolean appMarkDown = false;

	@BeanMeta(alias = "EnableAutoMarkDown")
	private volatile boolean enableAutoMarkDown = false;

	@BeanMeta(alias = "AutoMarkUpBatches")
	private volatile int autoMarkUpVolume = -1;

	@BeanMeta(alias = "MarkDownKeys")
	private volatile String markDownKeys = "";

	@BeanMeta(alias = "AutoMarkDowns")
	private volatile String autoMarkDowns="";
	
	@BeanMeta(alias = "AllInOneKeys")
	private volatile String allInOneKeys = "";

	@BeanMeta(alias = "AutoMarkUpSchedule")
	private volatile String autoMarkUpSchedule = "1,3,5";

	@BeanMeta(alias = "AutoMarkUpDelay")
	private volatile int autoMarkUpDelay = 120;

	@BeanMeta(omit = true)
	private int[] markUpSchedule = new int[] { 1, 3, 5 };

	@BeanMeta(omit = true)
	private Map<String, Markdown> marks = new HashMap<String, Markdown>();
	@BeanMeta(omit = true)
	private Set<String> autoDowns = new HashSet<String>();
	@BeanMeta(omit = true)
	private Lock lock = new ReentrantLock();

	public MarkdownConfigBean() {
		this.addChangeEvent("markDownKeys", new ChangeEvent() {

			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				if (newVal != null && !newVal.isEmpty())
				{
					String[] tokens = newVal.split(",");
					for (String token : tokens) {
						if (!getAllInOneKeys().contains(token))
							throw new Exception(
									String.format(
											"The specified database[%s] are not included, pls check",
											token));
					}
				}
			}

			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				updateMarks(newVal);
			}
		});

		this.addChangeEvent("autoMarkUpSchedule", new ChangeEvent() {
			@Override
			public void end(Object oldVal, String newVal) throws Exception {
				if (newVal == null || newVal.isEmpty())
					throw new Exception();
				String[] tokens = newVal.trim().split(",");
				int[] temp = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++) {
					temp[i] = Integer.parseInt(tokens[i]);
					if (temp[i] < 1 && temp[i] > 9) {
						throw new Exception(
								"The auto mark up schedule must be greater than 0 and lesser than 9");
					}
					if (i > 0 && temp[i] <= temp[i - 1]) {
						throw new Exception(
								"The auto mark up schedule must be ascending order");
					}
				}
				markUpSchedule = temp;
			}

			@Override
			public void before(Object oldVal, String newVal) throws Exception {
				// TODO Auto-generated method stub
			}
		});

		
	}

	public boolean isAppMarkDown() {
		return this.appMarkDown;
	}

	public void setAppMarkDown(boolean markdown) {
		this.appMarkDown = markdown;
	}

	public String getMarkDownKeys() {
		return this.markDownKeys;
	}

	public void setMarkDownKeys(String dbMarkdown) {
		this.markDownKeys = dbMarkdown;
	}

	public String getAllInOneKeys() {
		if (this.allInOneKeys.isEmpty())
			this.allInOneKeys = StringUtils.join(DalClientFactory.getAllDB(),
					",");
		return this.allInOneKeys;
	}

	public Markdown getMarkItem(String key) {
		return this.marks.get(key);
	}

	public Set<String> getMarks() {
		return this.marks.keySet();
	}

	public boolean isMarkdown(String dbname) {
		return this.isAppMarkDown() || this.marks.containsKey(dbname);
	}

	public synchronized void markdown(String dbname) {
		this.autoDowns.add(dbname);
		if(this.isEnableAutoMarkDown()){
			if (!this.marks.containsKey(dbname)) {
				this.marks.put(dbname, new Markdown(true));
				this.markDownKeys = StringUtils.join(this.getMarks(), ",");
			}
		}
	}

	public synchronized void markup(String dbname) {
		this.autoDowns.remove(dbname);
		if(this.isEnableAutoMarkDown()){
			if (this.marks.containsKey(dbname)) {
				this.marks.remove(dbname);
				this.markDownKeys = StringUtils.join(this.getMarks(), ",");
				MarkupManager.reset(dbname);
			}	
		}
	}

	private void updateMarks(String newVal) throws Exception {
		lock.lock();
		try {
			Map<String, Markdown> temp = new HashMap<String, Markdown>();
			if (newVal == null || newVal.isEmpty()) {
				for (String mark : this.getMarks()) {
					logger.info(String.format("Database %s has been marked up manually.", mark));
				}
				this.marks = temp;
				return;
			}
			String[] tokens = newVal.split(",");
			for (String token : tokens) {
				if (!this.marks.containsKey(token)) {
					logger.info(String.format("Database %s has been marked down manually.", token));
				}
				temp.put(token, new Markdown(false));
			}
			// If the current mark down database doesn't contain the new value
			// The new value need to be marked up on auto mark down
			for (String key : marks.keySet()) {
				if (!temp.containsKey(key)) {
					logger.info(String.format("Database %s has been marked up manually.", key));
				}
			}

			marks = temp; // Update reference
		} catch (Exception e) {
			lock.unlock();
			throw new Exception("Update mark downs bean failed.", e);
		} finally {
			lock.unlock();
		}
	}

	public boolean isEnableAutoMarkDown() {
		return enableAutoMarkDown;
	}

	public void setEnableAutoMarkDown(boolean enableAutoMarkDown) {
		this.enableAutoMarkDown = enableAutoMarkDown;
	}

	public int getAutoMarkUpVolume() {
		return autoMarkUpVolume;
	}

	public void setAutoMarkUpVolume(int autoMarkupBatches) {
		this.autoMarkUpVolume = autoMarkupBatches;
	}

	public int getAutoMarkUpDelay() {
		return autoMarkUpDelay;
	}

	public void setAutoMarkUpDelay(int autoMarkUpDelay) {
		this.autoMarkUpDelay = autoMarkUpDelay;
	}

	public String getAutoMarkUpSchedule() {
		return autoMarkUpSchedule;
	}

	public void setAutoMarkUpSchedule(String autoMarkUpSchedule) {
		this.autoMarkUpSchedule = autoMarkUpSchedule;
	}

	public int[] getMarkUpSchedule() {
		return markUpSchedule;
	}

	public String getAutoMarkDowns() {
		return StringUtils.join(this.autoDowns, ",");
	}
}
