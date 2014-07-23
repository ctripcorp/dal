package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaCodeGenContextCreator implements Processor {
	@Override
	public void process(Context context) {
		JavaCodeGenContext ctx = (JavaCodeGenContext) context;

		Project project = SpringBeanGetter.getDaoOfProject().getProjectByID(
				ctx.getProjectId());
		DalConfigHost dalConfigHost = null;
		if (project.getDal_config_name() != null
				&& !project.getDal_config_name().isEmpty()) {
			dalConfigHost = new DalConfigHost(project.getDal_config_name());
		} else if (project.getNamespace() != null
				&& !project.getNamespace().isEmpty()) {
			dalConfigHost = new DalConfigHost(project.getNamespace());
		} else {
			dalConfigHost = new DalConfigHost("");
		}
		ctx.setDalConfigHost(dalConfigHost);
		ctx.setNamespace(project.getNamespace());
	}

}
