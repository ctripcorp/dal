package com.ctrip.platform.dal.daogen.gen.cs;

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

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.gen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.gen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.pojo.DatabaseCategory;
import com.ctrip.platform.dal.daogen.pojo.DbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.pojo.StoredProcedure;
import com.ctrip.platform.dal.daogen.utils.DbUtils;
import com.ctrip.platform.dal.daogen.utils.JavaIOUtils;

public class CSharpGenerator extends AbstractGenerator {

	private CSharpGenerator() {

	}

	private static CSharpGenerator instance = new CSharpGenerator();

	public static CSharpGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateBySP(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateByFreeSql(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {

		List<CSharpTableHost> tableHosts = new ArrayList<CSharpTableHost>();
		List<CSharpTableHost> spHosts = new ArrayList<CSharpTableHost>();

		// 首先为所有表/存储过程生成DAO
		for (GenTaskByTableViewSp tableViewSp : tasks) {

			String dbName = tableViewSp.getDb_name();
			String[] tableNames =  StringUtils.split(tableViewSp.getTable_names(), ",");
			String[] spNames =  StringUtils.split(tableViewSp.getSp_names(),",");
			
			String prefix = tableViewSp.getPrefix();
			String suffix = tableViewSp.getSuffix();
			boolean pagination = tableViewSp.isPagination();
			boolean cud_by_sp = tableViewSp.isCud_by_sp();
			DbServer dbServer = dbServerDao.getDbServerByID(tableViewSp
					.getServer_id());
			DatabaseCategory dbCategory = DatabaseCategory.SqlServer;
			if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
				dbCategory = DatabaseCategory.MySql;
			}
			
			List<StoredProcedure> allSpNames = DbUtils.getAllSpNames(tableViewSp.getServer_id(), dbName);

			for (String table : tableNames) {
				CSharpTableHost tableHost = new CSharpTableHost();
				String className = table;
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				tableHost.setNameSpaceEntity(String.format(
						"%s.Entity.DataModel", super.namespace));
				tableHost.setNameSpaceIDao(String.format("%s.Interface.IDao",
						super.namespace));
				tableHost.setNameSpaceDao(String.format("%s.Dao",
						super.namespace));
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setTableName(table);
				tableHost.setClassName(className);
				tableHost.setTable(true);
				tableHost.setSpa(cud_by_sp);
				//SP方式增删改
				if(tableHost.isSpa()){
					CSharpSpInsertHost insertHost = CSharpSpInsertHost.getInsertSp(tableViewSp.getServer_id(),
							dbName, table, allSpNames);
					tableHost.setHasInsertMethod(insertHost.isHasInsertMethod());
					if(tableHost.isHasInsertMethod()){
						tableHost.setInsertMethodName(insertHost.getInsertMethodName());
						tableHost.setInsertParameterList(insertHost.getInsertParameterList());
					}
					
					CSharpSpUpdateHost updateHost = CSharpSpUpdateHost.getUpdateSp(tableViewSp.getServer_id(),
							dbName, table, allSpNames);
					tableHost.setHasUpdateMethod(updateHost.isHasUpdateMethod());
					if(tableHost.isHasUpdateMethod()){
						tableHost.setUpdateMethodName(updateHost.getUpdateMethodName());
						tableHost.setUpdateParameterList(updateHost.getUpdateParameterList());
					}
					
					CSharpSpDeleteHost deleteHost = CSharpSpDeleteHost.getDeleteSp(tableViewSp.getServer_id(),
							dbName, table, allSpNames);
					tableHost.setHasDeleteMethod(deleteHost.isHasDeleteMethod());
					if(tableHost.isHasDeleteMethod()){
						tableHost.setDeleteMethodName(deleteHost.getDeleteMethodName());
						tableHost.setDeleteParameterList(deleteHost.getDeleteParameterList());
					}
				}
				
				//主键及所有列
				List<String> primaryKeyNames = DbUtils.getPrimaryKeyNames(tableViewSp.getServer_id(), dbName, table);
				List<CSharpParameterHost> allColumns = DbUtils.getAllColumnNames(tableViewSp.getServer_id(), dbName, table);
				
				List<CSharpParameterHost> primaryKeys = new ArrayList<CSharpParameterHost>();
				for(CSharpParameterHost h : allColumns ){
					if(h.isNullable() && Consts.CSharpValueTypes.contains(h.getType())){
						h.setNullable(true);
					}else{
						h.setNullable(false);
					}
					if(primaryKeyNames.contains(h.getName())){
						h.setPrimary(true);
						primaryKeys.add(h);
					}
				}
				
				tableHost.setPrimaryKeys(primaryKeys);
				tableHost.setColumns(allColumns);
				
				tableHost.setHasPagination(pagination);
				
				StoredProcedure expectSptI = new StoredProcedure();
				expectSptI.setName(String.format("spT_%s_i", table));
				
				StoredProcedure expectSptU = new StoredProcedure();
				expectSptU.setName(String.format("spT_%s_u", table));
				
				StoredProcedure expectSptD = new StoredProcedure();
				expectSptD.setName(String.format("spT_%s_d", table));
				
				tableHost.setHasSptI(allSpNames.contains(expectSptI));
				tableHost.setHasSptU(allSpNames.contains(expectSptU));
				tableHost.setHasSptD(allSpNames.contains(expectSptD));
				tableHost.setHasSpt(tableHost.isHasSptI() || tableHost.isHasSptU() || tableHost.isHasSptD());

				tableHosts.add(tableHost);
			}
			
			for(String spName : spNames){
				String schema = "dbo";
				String realSpName = spName;
				if(spName.contains(".")){
					String[] splitSp = StringUtils.split(spName, '.');
					schema = splitSp[0];
					realSpName = splitSp[1];
				}
				
				StoredProcedure currentSp = new StoredProcedure();
				currentSp.setSchema(schema);
				currentSp.setName(realSpName);
				
				CSharpTableHost tableHost = new CSharpTableHost();
				String className = realSpName;
				if (null != prefix && !prefix.isEmpty()) {
					className = className.substring(prefix.length());
				}
				if (null != suffix && !suffix.isEmpty()) {
					className = className + suffix;
				}

				tableHost.setNameSpaceEntity(String.format(
						"%s.Entity.DataModel", super.namespace));
				tableHost.setNameSpaceDao(String.format("%s.Dao",
						super.namespace));
				tableHost.setDatabaseCategory(dbCategory);
				tableHost.setDbSetName(dbName);
				tableHost.setClassName(className);
				tableHost.setTable(false);
				tableHost.setSpName(spName);
				List<AbstractParameterHost> params =  DbUtils.getSpParams(tableViewSp.getServer_id(), dbName, currentSp, 0);
				List<CSharpParameterHost> realParams = new ArrayList<CSharpParameterHost>();
				for(AbstractParameterHost p : params){
					realParams.add((CSharpParameterHost) p);
				}
				tableHost.setSpParams(realParams);
				
				spHosts.add(tableHost);
			}

		}
		
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);
		for (CSharpTableHost host : tableHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter iDaoWriter = null;
			FileWriter pojoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				iDaoWriter = new FileWriter(String.format("%s/I%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/DAO.cs.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/Pojo.cs.tpl", "UTF-8",
						context, pojoWriter);
				Velocity.mergeTemplate("templates/IDAO.cs.tpl", "UTF-8",
						context, iDaoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
				JavaIOUtils.closeWriter(iDaoWriter);
			}
		}
		
		for (CSharpTableHost host : spHosts) {
			context.put("host", host);

			FileWriter daoWriter = null;
			FileWriter pojoWriter = null;
			try {
				File mavenLikeDir = new File(String.format("gen/%s/cs",
						projectId));
				FileUtils.forceMkdir(mavenLikeDir);

				daoWriter = new FileWriter(String.format("%s/%sDao.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));
				pojoWriter = new FileWriter(String.format("%s/%s.cs",
						mavenLikeDir.getAbsolutePath(), host.getClassName()));

				Velocity.mergeTemplate("templates/SpDAO.cs.tpl", "UTF-8",
						context, daoWriter);
				Velocity.mergeTemplate("templates/SpPojo.cs.tpl", "UTF-8",
						context, pojoWriter);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				JavaIOUtils.closeWriter(daoWriter);
				JavaIOUtils.closeWriter(pojoWriter);
			}
		}

	}

}
