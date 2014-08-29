package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.JavaTableHost;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaCodeGeneratorOfTableProcessor implements Processor {

	private static Logger log = Logger.getLogger(JavaCodeGeneratorOfTableProcessor.class);
	
	@Override
	public void process(Context context) {
		
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		String generatePath = CodeGenContext.generatePath;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", generatePath, projectId));
		
		List<Callable<ExecuteResult>> tableCallables = generateTableDao(ctx, dir);
		
		TaskUtils.invokeBatch(log, tableCallables);
	}
	
	private List<Callable<ExecuteResult>> generateTableDao(CodeGenContext codeGenCtx, final File mavenLikeDir) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		final Progress progress = ctx.getProgress();
		Queue<JavaTableHost> _tableHosts = ctx.get_tableHosts();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final JavaTableHost host : _tableHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Table[" + host.getDbName() + "." + host.getTableName() + "] Dao, Pojo, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/dao/standard/DAO.java.tpl");
	
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/Pojo.java.tpl");
	
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sDaoTest.java",
								mavenLikeDir.getAbsolutePath(),
								host.getPojoClassName()),
								"templates/java/DAOTest.java.tpl");
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sDaoUnitTest.java",
								mavenLikeDir.getAbsolutePath(),
								host.getPojoClassName()),
								"templates/java/DaoUnitTests.java.tpl");
						
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);

		}
		return results;
	}


}
