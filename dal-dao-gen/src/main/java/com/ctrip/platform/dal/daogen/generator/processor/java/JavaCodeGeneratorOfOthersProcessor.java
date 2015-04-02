package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;

import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.utils.GenUtils;

public class JavaCodeGeneratorOfOthersProcessor implements DalProcessor {
	
	@Override
	public void process(CodeGenContext context) throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));
		
		VelocityContext vltCcontext = GenUtils.buildDefaultVelocityContext();
		vltCcontext.put("host", ctx.getDalConfigHost());
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/Dal.config.tpl", dir.getAbsolutePath()),
				"templates/java/Dal.config.java.tpl");
		
		vltCcontext.put("host", ctx.getContextHost());
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/context.xml.tpl", dir.getAbsolutePath()),
				"templates/java/DalContext.java.tpl");
		
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/datasource.xml.tpl", dir.getAbsolutePath()),
				"templates/java/DataSource.java.tpl");
		
		vltCcontext.put("host", "");
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/ConfigProfile.xml", dir.getAbsolutePath()),
				"templates/java/ConfigProfile.java.tpl");
		
	}


}
