package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaCodeGeneratorOfFreeSqlProcessor implements Processor {

	private static Logger log = Logger.getLogger(JavaCodeGeneratorOfFreeSqlProcessor.class);
	
	public void process(Context context) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		String generatePath = CodeGenContext.generatePath;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", generatePath, projectId));
		
		List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(ctx, dir);
			
		TaskUtils.invokeBatch(log, freeCallables);
	}

	private List<Callable<ExecuteResult>> generateFreeSqlDao(CodeGenContext codeGenCtx, 
			final File mavenLikeDir) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		final Progress progress = ctx.getProgress();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Map<String, JavaMethodHost> _freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
		for (final JavaMethodHost host : _freeSqlPojoHosts.values()) {
			if(host.isSampleType())
				continue;
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getPojoClassName() + "] Pojo");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Entity/%s.java",
										mavenLikeDir.getAbsolutePath(),
										host.getPojoClassName()),
								"templates/java/Pojo.java.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + " exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}

		Queue<FreeSqlHost> _freeSqlHosts = ctx.get_freeSqlHosts();
		for (final FreeSqlHost host : _freeSqlHosts) {
			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/java/FreeSqlDAO.java.tpl");
	
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Test/%sDaoTest.java",
										mavenLikeDir.getAbsolutePath(),
										host.getClassName()),
								"templates/java/FreeSqlDAOTest.java.tpl");
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
