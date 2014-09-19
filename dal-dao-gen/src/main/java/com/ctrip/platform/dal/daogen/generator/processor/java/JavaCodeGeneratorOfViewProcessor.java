package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.ViewHost;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

public class JavaCodeGeneratorOfViewProcessor implements DalProcessor {
	
	private static Logger log = Logger.getLogger(JavaCodeGeneratorOfViewProcessor.class);

	@Override
	public void process(CodeGenContext context) throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));
		
		List<Callable<ExecuteResult>> viewCallables = generateViewDao(ctx, dir);
		
		TaskUtils.invokeBatch(log, viewCallables);
	}

	private List<Callable<ExecuteResult>> generateViewDao(CodeGenContext codeGenCtx, 
			final File mavenLikeDir) {
		
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		final Progress progress = ctx.getProgress();
		Queue<ViewHost> _viewHosts = ctx.getViewHosts();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final ViewHost host : _viewHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {					
					ExecuteResult result = new ExecuteResult("Generate View[" + host.getDbSetName() + "." + host.getViewName() + "] Dao");
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
								"templates/java/ViewDAO.java.tpl");
	
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
								"templates/java/test/DAOByViewTest.java.tpl");
						
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sDaoUnitTest.java",
								mavenLikeDir.getAbsolutePath(),
								host.getPojoClassName()),
								"templates/java/test/DAOByViewUnitTest.java.tpl");
						
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
