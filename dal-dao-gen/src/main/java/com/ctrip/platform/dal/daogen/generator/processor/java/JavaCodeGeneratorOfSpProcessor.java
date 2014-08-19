package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.SpDbHost;
import com.ctrip.platform.dal.daogen.host.java.SpHost;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaCodeGeneratorOfSpProcessor implements Processor {

	private static Logger log = Logger.getLogger(JavaCodeGeneratorOfSpProcessor.class);
	
	@Override
	public void process(Context context) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		String generatePath = CodeGenContext.generatePath;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", generatePath, projectId));
		
		List<Callable<ExecuteResult>> spCallables = generateSpDao(ctx, dir);
		
		TaskUtils.invokeBatch(log, spCallables);
	}

	private List<Callable<ExecuteResult>> generateSpDao(CodeGenContext codeGenCtx, 
			final File mavenLikeDir) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)codeGenCtx;
		final Progress progress = ctx.getProgress();
		Map<String, SpDbHost> _spHostMaps = ctx.get_spHostMaps();
		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		for (final SpDbHost host : _spHostMaps.values()) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

				@Override
				public ExecuteResult call() throws Exception {
					ExecuteResult result = new ExecuteResult("Generate SP[" + host.getDbName() + "] Dao, Pojo, Test");
					progress.setOtherMessage(result.getTaskName());
					try
					{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						
						GenUtils.mergeVelocityContext(
								context,
								String.format("%s/Dao/%sSpDao.java",
										mavenLikeDir.getAbsolutePath(),
										host.getDbName()),
								"templates/java/DAOBySp.java.tpl");
	
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sSpDaoTest.java",
								mavenLikeDir.getAbsolutePath(), host.getDbName()),
								"templates/java/DAOBySpTest.java.tpl");
						
						GenUtils.mergeVelocityContext(context, String.format(
								"%s/Test/%sSpDaoUnitTest.java",
								mavenLikeDir.getAbsolutePath(), host.getDbName()),
								"templates/java/DAOBySpUnitTest.java.tpl");
	
						for (SpHost sp : host.getSpHosts()) {
							context.put("host", sp);
							GenUtils.mergeVelocityContext(
									context,
									String.format("%s/Entity/%s.java",
											mavenLikeDir.getAbsolutePath(),
											sp.getPojoClassName()),
									"templates/java/Pojo.java.tpl");
						}
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
