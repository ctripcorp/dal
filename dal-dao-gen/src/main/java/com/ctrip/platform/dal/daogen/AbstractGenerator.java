package com.ctrip.platform.dal.daogen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject daoOfProject;

	protected static DaoBySqlBuilder daoBySqlBuilder;

	protected static DaoByFreeSql daoByFreeSql;

	protected static DaoByTableViewSp daoByTableViewSp;

	protected static String generatePath;
	
	protected static ExecutorService executor = Executors.newFixedThreadPool(100);

	protected String namespace;

	static {
		daoOfProject = SpringBeanGetter.getDaoOfProject();

		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();

		generatePath = Configuration.get("gen_code_path");

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	public void generate(int projectId, boolean regenerate, Progress progress) {
		
		Project proj = daoOfProject.getProjectByID(projectId);

		if (null != proj) {
			namespace = proj.getNamespace();
		}
		
		progress.setTotalFiles(4);
		
		prepareDirectory(projectId, regenerate);
		
		ProgressResource.addDoneFiles(progress, 1);
		
		prepareData(projectId, regenerate, progress);
		
		ProgressResource.addDoneFiles(progress, 2);
		
		generateCode(projectId, progress);
		
		ProgressResource.addDoneFiles(progress, 3);
		
		clearResource();
		
		ProgressResource.addDoneFiles(progress, 4);
	}

	public abstract boolean prepareDirectory(int projectId, boolean regenerate);
	
	public abstract boolean prepareData(int projectId, boolean regenerate, Progress progress);

	public abstract boolean generateCode(int projectId, Progress progress);

	public abstract boolean clearResource();


	protected String getPojoClassName(String prefix, String suffix, String table) {
		String className = table;
		if (null != prefix && !prefix.isEmpty()) {
			className = className.substring(prefix.length());
		}
		if (null != suffix && !suffix.isEmpty()) {
			className = className + WordUtils.capitalize(suffix);
		}

		StringBuilder result = new StringBuilder();
		for (String str : StringUtils.split(className, "_")) {
			result.append(WordUtils.capitalize(str));
		}

		return WordUtils.capitalize(result.toString());
	}

}
