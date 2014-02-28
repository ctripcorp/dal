package com.ctrip.platform.dal.daogen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.Project;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject daoOfProject;

	protected static DaoBySqlBuilder daoBySqlBuilder;

	protected static DaoByFreeSql daoByFreeSql;

	protected static DaoOfDbServer daoOfDbServer;
	
	protected static DaoByTableViewSp daoByTableViewSp;

	protected String namespace;

	protected int projectId;
	
	protected List<GenTaskByFreeSql> freeSqls;
	
	protected List<GenTaskByTableViewSp> tableViewSps;
	
	protected List<GenTaskBySqlBuilder> sqlBuilders;

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
	
	protected VelocityContext buildDefaultVelocityContext(){
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);
		
		return context;
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
			if(mavenLikeDir.exists())
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
		
		prepareFolder(projectId, "java");
		
		Map<String,String> dbs = new HashMap<String, String>();
		List<String> clazz = new ArrayList<String>();
		
		freeSqls = daoByFreeSql.getTasksByProjectId(projectId);
		tableViewSps = daoByTableViewSp.getTasksByProjectId(projectId);
		sqlBuilders = daoBySqlBuilder.getTasksByProjectId(projectId);
		
		for(GenTaskByFreeSql task : freeSqls){
			if(!dbs.containsKey(task.getDb_name())){
				DbServer dbServer = daoOfDbServer.getDbServerByID(task.getServer_id());
				String provider = "sqlProvider";
				if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}
		}
		for(GenTaskByTableViewSp task : tableViewSps){
			if(!dbs.containsKey(task.getDb_name())){
				DbServer dbServer = daoOfDbServer.getDbServerByID(task.getServer_id());
				String provider = "sqlProvider";
				if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
					provider = "mySqlProvider";
				}
				dbs.put(task.getDb_name(), provider);
			}
			for(String t : StringUtils.split(task.getTable_names(), ",")){
				if(!clazz.contains(t)){
					String className = t;
					if (null != task.getPrefix() && !task.getPrefix().isEmpty()) {
						className = className.substring(task.getPrefix().length());
					}
					if (null != task.getSuffix() && !task.getSuffix() .isEmpty()) {
						className = className + task.getSuffix() ;
					}
					clazz.add(className);
				}
			}
		}
		
		VelocityContext context = new VelocityContext();
		
		context.put("dbs", dbs);
		context.put("clazzList", clazz);
		context.put("namespace", namespace);
		context.put("WordUtils", WordUtils.class);
		
		FileWriter dalConfigWriter = null;
		FileWriter dalFactoryWriter = null;
		FileWriter dalProgramWriter = null;
		try {
		
			dalConfigWriter = new FileWriter(String.format("%s/Dal.config",
					mavenLikeDir.getAbsolutePath()));
			dalFactoryWriter = new FileWriter(String.format("%s/DalFactory.cs",
					mavenLikeDir.getAbsolutePath()));
			dalProgramWriter = new FileWriter(String.format("%s/Program.cs",
					mavenLikeDir.getAbsolutePath()));

			Velocity.mergeTemplate("templates/Dal.config.tpl", "UTF-8",
					context, dalConfigWriter);
			Velocity.mergeTemplate("templates/DalFactory.cs.tpl", "UTF-8",
					context, dalFactoryWriter);
			Velocity.mergeTemplate("templates/Program.cs.tpl", "UTF-8",
					context, dalProgramWriter);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaIOUtils.closeWriter(dalConfigWriter);
			JavaIOUtils.closeWriter(dalFactoryWriter);
			JavaIOUtils.closeWriter(dalProgramWriter);
		}

		generateByTableView(tableViewSps);

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
		

		generateByFreeSql(freeSqls);

		return true;

	}
	
	private void prepareFolder(int projectId, String lang) {
		File mavenLikeDir = new File(String.format("gen/%s/%s",
				projectId, lang));

		try {
			if(mavenLikeDir.exists())
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

	}

	@Override
	public abstract void generateByTableView(List<GenTaskByTableViewSp> tasks);

	@Override
	public abstract void generateByFreeSql(List<GenTaskByFreeSql> tasks);
	
	@Override
	public abstract void generateBySqlBuilder(List<GenTask> tasks);

}