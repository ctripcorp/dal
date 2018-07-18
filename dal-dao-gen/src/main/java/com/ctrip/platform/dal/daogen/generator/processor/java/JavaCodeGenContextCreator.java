package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

public class JavaCodeGenContextCreator implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            JavaCodeGenContext ctx = (JavaCodeGenContext) context;
            Project project = BeanGetter.getDaoOfProject().getProjectByID(ctx.getProjectId());
            DalConfigHost dalConfigHost = null;
            if (project.getDal_config_name() != null && !project.getDal_config_name().isEmpty()) {
                dalConfigHost = new DalConfigHost(project.getDal_config_name());
            } else if (project.getNamespace() != null && !project.getNamespace().isEmpty()) {
                dalConfigHost = new DalConfigHost(project.getNamespace());
            } else {
                dalConfigHost = new DalConfigHost("");
            }
            ctx.setDalConfigHost(dalConfigHost);
            ctx.setNamespace(project.getNamespace());
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
