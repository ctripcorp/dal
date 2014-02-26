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
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject daoOfProject;

	protected static DaoBySqlBuilder daoBySqlBuilder;

	protected static DaoByFreeSql daoByFreeSql;

	protected static DaoOfDbServer daoOfDbServer;
	
	protected static DaoByTableViewSp daoByTableViewSp;

	protected String namespace;

	protected int projectId;

	static {

		daoOfProject = SpringBeanGetter.getDaoOfProject();
		daoBySqlBuilder = SpringBeanGetter.getDaoBySqlBuilder();
		daoByFreeSql = SpringBeanGetter.getDaoByFreeSql();
		daoOfDbServer = SpringBeanGetter.getDaoOfDbServer();
		daoByTableViewSp = SpringBeanGetter.getDaoByTableViewSp();

		java.util.Properties pr = new java.util.Properties();
		pr.setProperty("resource.loader", "class");
		pr.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init(pr);
	}

	@Override
	public boolean generateCode(int projectId) {

		Project proj = daoOfProject.getProjectByID(projectId);

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}
		
		
		File mavenLikeDir = new File(String.format("gen/%s/cs",
				projectId));
		
		try {
			FileUtils.forceDelete(mavenLikeDir);
			//FileUtils.forceMkdir(mavenLikeDir);
			File daoMavenLike = new File(mavenLikeDir, "Dao");
			File entityMavenLike = new File(mavenLikeDir, "Entity");
			File idaoMavenLike = new File(mavenLikeDir, "IDao");
			
			FileUtils.forceMkdir(daoMavenLike);
			FileUtils.forceMkdir(entityMavenLike);
			FileUtils.forceMkdir(idaoMavenLike);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		generateByTableView(daoByTableViewSp.getTasksByProjectId(projectId));

//		// 存储过程
//		List<GenTaskBySP> sp = spTaskDao.getTasksByProjectId(Integer
//				.valueOf(projectId));
//		List<GenTask> _sp = new ArrayList<GenTask>();
//		for (GenTaskBySP t : sp) {
//			_sp.add(t);
//		}
//		generateBySP(_sp);
//
		// 手工编写的SQL
		List<GenTaskByFreeSql> freeSql = daoByFreeSql.getTasksByProjectId(projectId);

		generateByFreeSql(freeSql);

		return true;

	}

	@Override
	public abstract void generateByTableView(List<GenTaskByTableViewSp> tasks);

	@Override
	public abstract void generateByFreeSql(List<GenTaskByFreeSql> tasks);
	
	@Override
	public abstract void generateBySqlBuilder(List<GenTask> tasks);

}