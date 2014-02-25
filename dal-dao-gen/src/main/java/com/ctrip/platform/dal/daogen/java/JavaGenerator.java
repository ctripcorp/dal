package com.ctrip.platform.dal.daogen.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.cs.CSharpMethodHost;
import com.ctrip.platform.dal.daogen.cs.CSharpParameterHost;
import com.ctrip.platform.dal.daogen.cs.CSharpSpDeleteHost;
import com.ctrip.platform.dal.daogen.cs.CSharpSpInsertHost;
import com.ctrip.platform.dal.daogen.cs.CSharpSpUpdateHost;
import com.ctrip.platform.dal.daogen.cs.CSharpTableHost;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.CurrentLanguage;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskBySqlBuilder;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class JavaGenerator extends AbstractGenerator {

	private JavaGenerator() {

	}

	private static JavaGenerator instance = new JavaGenerator();
	private static DaoOfDbServer dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDaoOfDbServer();
	}

	public static JavaGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {
		
		List<JavaTableHost> tableHosts = new ArrayList<JavaTableHost>();

		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {

			String dbName = tableViewSp.getDb_name();
			String[] tableNames = StringUtils.split(
					tableViewSp.getTable_names(), ",");

			String prefix = tableViewSp.getPrefix();
			String suffix = tableViewSp.getSuffix();
			boolean cud_by_sp = tableViewSp.isCud_by_sp();
			DbServer dbServer = daoOfDbServer.getDbServerByID(tableViewSp
					.getServer_id());
			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
				dbCategory = DatabaseCategory.MySql;
			}

			List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(
					tableViewSp.getServer_id(), dbName);

			for (String table : tableNames) {

				// 主键及所有列
				List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(
						tableViewSp.getServer_id(), dbName, table);
				List<AbstractParameterHost> allColumnsAbstract = DbUtils
						.getAllColumnNames(tableViewSp.getServer_id(), dbName,
								table, CurrentLanguage.Java);
				
				List<JavaParameterHost> allColumns = new ArrayList<JavaParameterHost>();
				for(AbstractParameterHost h : allColumnsAbstract){
					allColumns.add((JavaParameterHost)h);
				}

				List<JavaParameterHost> primaryKeys = new ArrayList<JavaParameterHost>();
				boolean hasIdentity = false;
				String identityColumnName = null;
				for (JavaParameterHost h : allColumns) {
					if(!hasIdentity && h.isIdentity()){
						hasIdentity = true;
						identityColumnName = h.getName();
					}
					if (primaryKeyNames.contains(h.getName())) {
						h.setPrimary(true);
						primaryKeys.add(h);
					}
				}

				String className = table;
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				JavaTableHost tableHost = new JavaTableHost();
				tableHost.setNamespace(super.namespace);
				tableHost.setDbName(dbName);
				tableHost.setTableName(table);
				tableHost.setPojoClassName(className);
				tableHost.setFields(allColumns);
				tableHost.setHasIdentity(hasIdentity);
				tableHost.setIdentityColumnName(identityColumnName);
				tableHost.setSpa(cud_by_sp);

				tableHosts.add(tableHost);
			}
		}
		
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);

		for (JavaTableHost host : tableHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/java",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.java",
						mavenLikeDir.getAbsolutePath(), host.getPojoClassName()));

				Velocity.mergeTemplate("templates/DAO.java.tpl", "UTF-8",
						context, daoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
			}
		}
		
	}

	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}
	
}