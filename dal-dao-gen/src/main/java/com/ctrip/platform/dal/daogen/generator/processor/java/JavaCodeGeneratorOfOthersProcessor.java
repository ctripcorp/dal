package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;

import org.apache.velocity.VelocityContext;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaCodeGeneratorOfOthersProcessor implements Processor {
	
	@Override
	public void process(Context context) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		String generatePath = CodeGenContext.generatePath;
		int projectId = ctx.getProjectId();
		File dir = new File(String.format("%s/%s/java", generatePath, projectId));
		
		VelocityContext vltCcontext = GenUtils.buildDefaultVelocityContext();
		vltCcontext.put("host", ctx.getDalConfigHost());
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/Dal.config.tpl", dir.getAbsolutePath()),
				"templates/java/Dal.config.java.tpl");
		
		vltCcontext.put("host", ctx.getContextHost());
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/context.xml.tpl", dir.getAbsolutePath()),
				"templates/java/DalContext.java.tpl");
		
		vltCcontext.put("host", "");
		GenUtils.mergeVelocityContext(vltCcontext,
				String.format("%s/ConfigProfile.xml", dir.getAbsolutePath()),
				"templates/java/ConfigProfile.java.tpl");
		
	}


}
