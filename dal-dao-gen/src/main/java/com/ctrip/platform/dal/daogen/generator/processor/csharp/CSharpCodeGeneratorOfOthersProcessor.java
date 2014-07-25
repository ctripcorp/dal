package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.io.File;

import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class CSharpCodeGeneratorOfOthersProcessor implements Processor {
	
	@Override
	public void process(Context context) {
		
		generateCommonCode((CodeGenContext )context);
	}

	/**
	 * 生成C#的公共部分，如Dal.config，Program.cs以及DALFactory.cs
	 */
	private void generateCommonCode(CodeGenContext codeGenCtx) {
		
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)codeGenCtx;
		
		final int id = ctx.getProjectId();

		final VelocityContext context = GenUtils.buildDefaultVelocityContext();

		final File csMavenLikeDir = new File(String.format("%s/%s/cs",
				CodeGenContext.generatePath, id));
		context.put("host", ctx.getDalConfigHost());
		context.put("dbs", ctx.get_dbHosts().values());
		context.put("namespace", ctx.getNamespace());
		context.put("freeSqlHosts", ctx.get_freeDaos());
		context.put("tableHosts", ctx.get_tableDaos());
		context.put("spHosts", ctx.get_spDaos());

		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/Config/Dal.config.tpl",
						csMavenLikeDir.getAbsolutePath()),
				"templates/csharp/DalConfig.cs.tpl");
		
		GenUtils.mergeVelocityContext(
				context,
				String.format("%s/DalFactory.cs",
						csMavenLikeDir.getAbsolutePath()),
				"templates/csharp/DalFactory.cs.tpl");
	}

}
