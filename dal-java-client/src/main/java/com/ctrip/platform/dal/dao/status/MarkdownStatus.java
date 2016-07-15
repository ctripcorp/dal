package com.ctrip.platform.dal.dao.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalClientFactory;

public class MarkdownStatus extends BaseStatus implements MarkdownStatusMBean {

	private volatile boolean appMarkDown = false;

	private volatile boolean enableAutoMarkDown = false;

	private volatile int autoMarkUpBatches = -1;

	private volatile int autoMarkUpDelay = 120;

	private volatile Integer[] autoMarkUpSchedule = new Integer[] { 1, 3, 5 };

	public boolean isAppMarkDown() {
		return this.appMarkDown;
	}

	public void setAppMarkDown(boolean markdown) {
		this.appMarkDown = markdown;
		changed();
	}

	//TODO unify to MarkdownManager
	public boolean isMarkdown(String dbname) {
		return this.isAppMarkDown() || 
				DalStatusManager.getDataSourceStatus(dbname).isManualMarkdown() ||
				(enableAutoMarkDown && DalStatusManager.getDataSourceStatus(dbname).isAutoMarkdown());
	}

	public boolean isEnableAutoMarkDown() {
		return enableAutoMarkDown;
	}

	public void setEnableAutoMarkDown(boolean enableAutoMarkDown) {
		this.enableAutoMarkDown = enableAutoMarkDown;
		changed();
	}

	public int getAutoMarkUpVolume() {
		return autoMarkUpBatches;
	}

	public void setAutoMarkUpVolume(int autoMarkupBatches) {
		this.autoMarkUpBatches = autoMarkupBatches;
		changed();
	}

	public int getAutoMarkUpDelay() {
		return autoMarkUpDelay;
	}

	public void setAutoMarkUpDelay(int autoMarkUpDelay) {
		this.autoMarkUpDelay = autoMarkUpDelay;
		changed();
	}

	public String getAutoMarkUpSchedule() {
		return StringUtils.join(Arrays.asList(autoMarkUpSchedule), ",");
	}

	public void setAutoMarkUpSchedule(String newAutoMarkUpSchedule) throws Exception {
		autoMarkUpSchedule = parseSchedule(newAutoMarkUpSchedule);
		changed();
	}

	private Integer[] parseSchedule(String newAutoMarkUpSchedule) throws Exception {
		if (newAutoMarkUpSchedule == null || newAutoMarkUpSchedule.isEmpty())
			throw new Exception("The value can't be empty");
		String[] tokens = newAutoMarkUpSchedule.trim().split(",");
		Integer[] temp = new Integer[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			temp[i] = Integer.parseInt(tokens[i]);
			if (temp[i] < 1 || temp[i] > 9) {
				throw new Exception(
						"The auto mark up schedule must be greater than 0 and lesser than 9");
			}
			if (i > 0 && temp[i] <= temp[i - 1]) {
				throw new Exception(
						"The auto mark up schedule must be ascending order");
			}
		}
		return temp;
	}

	public Integer[] getMarkUpSchedule() {
		return autoMarkUpSchedule;
	}

	public String getMarkDownKeys() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			if(isMarkdown(dbName))
				names.add(dbName);
		}

		return StringUtils.join(names, ",");
	}

	public String getDataSourceNames() {
		return StringUtils.join(DalClientFactory.getDalConfigure().getDataSourceNames(), ",");
	}

	public String getDatabaseSetNames() {
		return StringUtils.join(DalClientFactory.getDalConfigure().getDatabaseSetNames(), ",");
	}
	
	public String getAutoMarkDowns() {
		Set<String> names = new HashSet<>();
		for(String dbName: DalClientFactory.getDalConfigure().getDataSourceNames()){
			DataSourceStatus dss = DalStatusManager.getDataSourceStatus(dbName);
			if(dss.isAutoMarkdown())
				names.add(dbName);
		}
		return StringUtils.join(names, ",");
	}
}
