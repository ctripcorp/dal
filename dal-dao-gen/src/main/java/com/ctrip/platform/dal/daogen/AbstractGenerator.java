
package com.ctrip.platform.dal.daogen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.common.util.Configuration;
import com.ctrip.platform.dal.daogen.cs.DatabaseHost;
import com.ctrip.platform.dal.daogen.dao.DaoByFreeSql;
import com.ctrip.platform.dal.daogen.dao.DaoBySqlBuilder;
import com.ctrip.platform.dal.daogen.dao.DaoByTableViewSp;
import com.ctrip.platform.dal.daogen.dao.DaoOfProject;
import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public abstract class AbstractGenerator implements Generator {

	protected static DaoOfProject daoOfProject;

	protected static DaoBySqlBuilder daoBySqlBuilder;

	protected static DaoByFreeSql daoByFreeSql;

	protected static DaoByTableViewSp daoByTableViewSp;

	protected static String generatePath;

	protected String namespace;

	protected int projectId;

	protected boolean regenerate;

	protected Map<String, DatabaseHost> dbHosts;

	protected Set<String> tableDaos;

	protected Set<String> spDaos;

	protected Set<String> freeDaos;

	protected List<GenTaskByFreeSql> freeSqls;

	protected List<GenTaskByTableViewSp> tableViewSps;

	protected List<GenTaskBySqlBuilder> sqlBuilders;

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

	@Override
	public boolean generateCode(int projectId, boolean regenerate, Progress progress)
			throws Exception {

		this.regenerate = regenerate;

		Project proj = daoOfProject.getProjectByID(projectId);

		dbHosts = new HashMap<String, DatabaseHost>();
		tableDaos = new HashSet<String>();
		spDaos = new HashSet<String>();
		freeDaos = new HashSet<String>();

		if (null != proj) {
			namespace = proj.getNamespace();
			this.projectId = projectId;
		}

		if (regenerate) {
			freeSqls = daoByFreeSql.updateAndGetAllTasks(projectId);
			tableViewSps = daoByTableViewSp.updateAndGetAllTasks(projectId);
			sqlBuilders = daoBySqlBuilder.updateAndGetAllTasks(projectId);

			buildDbs(freeSqls, tableViewSps, sqlBuilders);
		} else {
			freeSqls = daoByFreeSql.updateAndGetTasks(projectId);
			tableViewSps = daoByTableViewSp.updateAndGetTasks(projectId);
			sqlBuilders = daoBySqlBuilder.updateAndGetTasks(projectId);

			buildDbs(daoByFreeSql.getTasksByProjectId(projectId),
					daoByTableViewSp.getTasksByProjectId(projectId),
					daoBySqlBuilder.getTasksByProjectId(projectId));
		}

		generateByTableView(tableViewSps,progress);

		generateByFreeSql(freeSqls,progress);

		return true;
	}

	private void buildDbs(List<GenTaskByFreeSql> _freeSqls,
			List<GenTaskByTableViewSp> _tableViewSps,
			List<GenTaskBySqlBuilder> _sqlBuilders) {
		
		Set<String> existsTable = new HashSet<String>();

		for (GenTaskByFreeSql task : _freeSqls) {
			freeDaos.add(WordUtils.capitalize(task.getClass_name()));
			if (!dbHosts.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
//				int index = host.getAllInOneName().indexOf("_");
//				host.setDatasetName(host.getAllInOneName().substring(0,
//						index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				dbHosts.put(task.getDb_name(), host);
			}
		}
		for (GenTaskByTableViewSp task : _tableViewSps) {
			for (String table : StringUtils.split(task.getTable_names(), ",")) {
				tableDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), table));
				existsTable.add(table);
			}
			for (String table : StringUtils.split(task.getView_names(), ",")) {
				tableDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), table));
			}
			for (String table : StringUtils.split(task.getSp_names(), ",")) {
				String realSpName = table;
				if (table.contains(".")) {
					String[] splitSp = StringUtils.split(table, '.');
					realSpName = splitSp[1];
				}
				spDaos.add(getPojoClassName(task.getPrefix(),
						task.getSuffix(), realSpName.replace("_", "")));
			}
			
			if (!dbHosts.containsKey(task.getDb_name())) {

				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
//				int index = host.getAllInOneName().indexOf("_");
//				host.setDatasetName(host.getAllInOneName().substring(0,
//						index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				dbHosts.put(task.getDb_name(), host);
			}
		}

		for (GenTaskBySqlBuilder task : _sqlBuilders) {
			
			if(!existsTable.contains(task.getTable_name())){
				tableDaos.add(getPojoClassName("",
						"Gen", task.getTable_name()));
			}
			
			if (!dbHosts.containsKey(task.getDb_name())) {
				String provider = "sqlProvider";
				if (!DbUtils.getDbType(task.getDb_name()).equalsIgnoreCase(
						"Microsoft SQL Server")) {
					provider = "mySqlProvider";
				}
				DatabaseHost host = new DatabaseHost();
				host.setAllInOneName(task.getDb_name());
				host.setProviderType(provider);
//				int index = host.getAllInOneName().indexOf("_");
//				host.setDatasetName(host.getAllInOneName().substring(0,
//						index > -1 ? index : host.getAllInOneName().length()));
				host.setDatasetName(host.getAllInOneName());
				dbHosts.put(task.getDb_name(), host);
			}
		}

	}

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

	protected void prepareFolder(int projectId, String lang) {
		File mavenLikeDir = new File(String.format("%s/%s/%s", generatePath,
				projectId, lang));

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
	public abstract void generateByTableView(List<GenTaskByTableViewSp> tasks,Progress progress)
			throws Exception;

	@Override
	public abstract void generateByFreeSql(List<GenTaskByFreeSql> tasks,Progress progress)
			throws Exception;

}
