package com.ctrip.platform.dal.daogen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject daoOfProject;

	protected static DaoBySqlBuilder daoBySqlBuilder;

	protected static DaoByFreeSql daoByFreeSql;

	protected static DaoOfDbServer daoOfDbServer;

	protected static DaoByTableViewSp daoByTableViewSp;

	protected String namespace;

	protected int projectId;

	protected boolean regenerate;

	protected List<GenTaskByFreeSql> freeSqls;

	protected List<GenTaskByTableViewSp> tableViewSps;

	protected List<GenTaskBySqlBuilder> sqlBuilders;

	static {
		daoOfProject = SpringBeanGetter.getDaoOfProject();
		daoOfDbServer = SpringBeanGetter.getDaoOfDbServer();

		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(int projectId, boolean regenerate)  throws Exception{

		this.regenerate = regenerate;

		Project proj = daoOfProject.getProjectByID(projectId);

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}

		if (regenerate) {
			freeSqls = daoByFreeSql.updateAndGetAllTasks(projectId);
			tableViewSps = daoByTableViewSp.updateAndGetAllTasks(projectId);
			sqlBuilders = daoBySqlBuilder.updateAndGetAllTasks(projectId);
		} else {
			freeSqls = daoByFreeSql.updateAndGetTasks(projectId);
			tableViewSps = daoByTableViewSp.updateAndGetTasks(projectId);
			sqlBuilders = daoBySqlBuilder.updateAndGetTasks(projectId);
		}

		generateByTableView(tableViewSps);

		generateByFreeSql(freeSqls);

		return true;
	}

	protected void prepareFolder(int projectId, String lang) {
		File mavenLikeDir = new File(
				String.format("gen/%s/%s", projectId, lang));

		try {
			if (mavenLikeDir.exists() && this.regenerate)
				FileUtils.forceDelete(mavenLikeDir);

			if (lang.equals("cs")) {
				File idaoMavenLike = new File(mavenLikeDir, "IDao");
				if (!idaoMavenLike.exists()) {
					FileUtils.forceMkdir(idaoMavenLike);
				}
			}
			File daoMavenLike = new File(mavenLikeDir, "Dao");
			File entityMavenLike = new File(mavenLikeDir, "Entity");
			File testMavenLike = new File(mavenLikeDir, "Test");

			if (!daoMavenLike.exists()) {
				FileUtils.forceMkdir(daoMavenLike);
			}
			if (!entityMavenLike.exists()) {
				FileUtils.forceMkdir(entityMavenLike);
			}
			if (!testMavenLike.exists()) {
				FileUtils.forceMkdir(testMavenLike);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public abstract void generateByTableView(List<GenTaskByTableViewSp> tasks) throws Exception;

	@Override
	public abstract void generateByFreeSql(List<GenTaskByFreeSql> tasks) throws Exception;

}
