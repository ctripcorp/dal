package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlPojoHost;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

public class CSharpCodeGeneratorOfFreeSqlProcessor implements DalProcessor {

	private static Logger log = Logger.getLogger(CSharpCodeGeneratorOfFreeSqlProcessor.class);
	
	@Override
	public void process(CodeGenContext context) throws Exception {
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)context;
		int projectId = ctx.getProjectId();
		
		final File dir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), projectId));
		
		List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(ctx,dir);
		
		TaskUtils.invokeBatch(log, freeCallables);
	}
	
	private List<Callable<ExecuteResult>> generateFreeSqlDao(CodeGenContext codeGenCtx,
			final File mavenLikeDir) {
		
		final CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final Progress progress = ctx.getProgress();

		List<Callable<ExecuteResult>> results = new ArrayList<Callable<ExecuteResult>>();

		Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = ctx.getFreeSqlPojoHosts();
		
		for (final CSharpFreeSqlPojoHost host : _freeSqlPojoHosts.values()) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Pojo");
					progress.setOtherMessage(result.getTaskName());
					try{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Entity/%s.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
										ctx.isNewPojo() ? "templates/csharp/PojoNew.cs.tpl" : "templates/csharp/Pojo.cs.tpl");
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);
		}
		ProgressResource.addDoneFiles(progress, _freeSqlPojoHosts.size());

		Queue<CSharpFreeSqlHost> _freeSqlHosts = ctx.getFreeSqlHosts();
		for (final CSharpFreeSqlHost host : _freeSqlHosts) {

			Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
				@Override
				public ExecuteResult call() {
					//progress.setOtherMessage("正在生成 " + host.getClassName());
					ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
					progress.setOtherMessage(result.getTaskName());
					try{
						VelocityContext context = GenUtils.buildDefaultVelocityContext();
						context.put("host", host);
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Dao/%sDao.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils.normalizeVariable(host.getClassName())),
								"templates/csharp/dao/freesql/FreeSqlDAO.cs.tpl");
	
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Test/%sTest.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
								"templates/csharp/test/FreeSqlTest.cs.tpl");
						
						GenUtils.mergeVelocityContext(context,
								String.format("%s/Test/%sUnitTest.cs", mavenLikeDir
										.getAbsolutePath(), CommonUtils
										.normalizeVariable(host.getClassName())),
								"templates/csharp/test/FreeSqlUnitTest.cs.tpl");
						
						result.setSuccessal(true);
					}catch(Exception e){
						log.error(result.getTaskName() + "exception", e);
					}
					return result;
				}
			};
			results.add(worker);

		}

		return results;
	}


}
